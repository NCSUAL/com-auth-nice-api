package umonaz.omnibus.service.nice;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import umonaz.omnibus.account.domain.credential.NiceSymmetricSalt;
import umonaz.omnibus.account.domain.exception.AccountAuthenticationValidationFailedException;
import umonaz.omnibus.account.domain.exception.AccountNiceApiInternalServerException;
import umonaz.omnibus.account.domain.model.command.NiceCallbackCommand;
import umonaz.omnibus.account.domain.port.in.nice.GetNiceSymmetricContextService;
import umonaz.omnibus.account.domain.port.in.nice.NiceVerificationService;
import umonaz.omnibus.account.domain.service.NiceAccountService;
import umonaz.omnibus.auth.domain.nice.NiceVerifiedUser;
import umonaz.omnibus.client.properties.NicePepperProperties;
import umonaz.omnibus.client.properties.NiceProperties;
import umonaz.omnibus.client.request.NiceAuthResultRequest;
import umonaz.omnibus.client.response.NiceAuthResultResponse;
import umonaz.omnibus.service.crypto.NiceAesCbcCryptoService;

@Service
@RequiredArgsConstructor
public class NiceAccountFacade implements NiceAccountService {

    private final GetNiceSymmetricContextService getNiceSymmetricContextService;
    private final NiceVerificationService niceVerificationService;
    private final NiceAesCbcCryptoService niceAesGcmCryptoService;
    private final NiceHttpCredentialFacade niceHttpCredentialFacade;
    private final NiceProperties niceProperties;
    private final WebClient webClient;
    private final NicePepperProperties nicePepperProperties;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> authenticate(NiceCallbackCommand niceCallbackCommand) {
        return getNiceSymmetricContextService
                .execute(niceCallbackCommand.transactionId())
                .flatMap(context -> requestAuthResult(
                                context.accessToken(),
                                niceCallbackCommand.webTransactionId(),
                                context.niceTransactionId(),
                                context.requestNo())
                        .flatMap(resultResponse -> {
                            NiceSymmetricSalt salt = niceHttpCredentialFacade.deriveSymmetricSalt(
                                    context.ticket(), context.niceTransactionId(), context.iterators());

                            niceVerificationService.verify(
                                    salt, resultResponse.encData(), resultResponse.integrityValue());

                            try {
                                String decryptedPayload =
                                        niceAesGcmCryptoService.decrypt(resultResponse.encData(), salt);
                                NiceVerifiedUser verifiedUser =
                                        objectMapper.readValue(decryptedPayload, NiceVerifiedUser.class);
                                return Mono.just(verifiedUser);
                            } catch (Exception e) {
                                return Mono.error(new AccountAuthenticationValidationFailedException());
                            }
                        }))
                .flatMap(this::processEnrollUser);
    }

    private Mono<NiceAuthResultResponse> requestAuthResult(
            String accessToken, String webTransactionId, String transactionId, String requestNo) {
        NiceAuthResultRequest requestBody = NiceAuthResultRequest.builder()
                .webTransactionId(webTransactionId)
                .transactionId(transactionId)
                .requestNo(requestNo)
                .build();

        return webClient
                .post()
                .uri(niceProperties.authResultRequestUrl())
                .header(HttpHeaders.AUTHORIZATION, niceProperties.generateBearerAuthHeader(accessToken))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(NiceAuthResultResponse.class)
                .onErrorMap(e -> new AccountNiceApiInternalServerException());
    }

    private Mono<Void> processEnrollUser(final NiceVerifiedUser niceVerifiedUser) {
        return webClient
                .post()
                .uri(nicePepperProperties.requestUrl())
                .bodyValue(niceVerifiedUser)
                .retrieve()
                .bodyToMono(Void.class);
    }
}
