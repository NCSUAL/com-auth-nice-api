package umonaz.omnibus.service.nice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import umonaz.omnibus.account.domain.credential.NiceSymmetricSalt;
import umonaz.omnibus.account.domain.exception.AccountAuthenticationValidationFailedException;
import umonaz.omnibus.account.domain.exception.AccountNiceApiInternalServerException;
import umonaz.omnibus.account.domain.model.command.NiceCallbackCommand;
import umonaz.omnibus.account.domain.model.symmetric.NiceSymmetricContext;
import umonaz.omnibus.account.domain.port.NiceSymmetricContextDeleter;
import umonaz.omnibus.account.domain.port.in.nice.GetNiceSymmetricContextService;
import umonaz.omnibus.account.domain.port.in.nice.NiceVerificationService;
import umonaz.omnibus.account.domain.service.NiceAccountService;
import umonaz.omnibus.auth.domain.nice.NiceVerifiedUser;
import umonaz.omnibus.client.properties.NiceProperties;
import umonaz.omnibus.client.request.NiceAuthResultRequest;
import umonaz.omnibus.client.response.NiceAuthResultResponse;
import umonaz.omnibus.service.crypto.NiceAesGcmCryptoService;

@Service
@RequiredArgsConstructor
@Slf4j
public class NiceAccountFacade implements NiceAccountService {

    /** 본인인증 결과 페이로드의 성공 코드 */
    private static final String RESULT_CODE_SUCCESS = "0000";

    private final GetNiceSymmetricContextService getNiceSymmetricContextService;
    private final NiceSymmetricContextDeleter niceSymmetricContextDeleter;
    private final NiceVerificationService niceVerificationService;
    private final NiceAesGcmCryptoService niceAesGcmCryptoService;
    private final NiceHttpCredentialFacade niceHttpCredentialFacade;
    private final NiceProperties niceProperties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 본인인증 콜백을 검증하고 확인된 사용자를 돌려준다.
     */
    @Override
    public NiceVerifiedUser authenticate(NiceCallbackCommand niceCallbackCommand) {

        // requestNo로 Redis 조회
        NiceSymmetricContext context = getNiceSymmetricContextService.execute(niceCallbackCommand.transactionId());

        // 한 번 꺼낸 컨텍스트는 결과와 무관하게 태운다. 재사용 통로를 여기서 닫는다.
        niceSymmetricContextDeleter.deleteById(niceCallbackCommand.transactionId());

        // NICE의 transaction_id를 result API에 전달
        NiceAuthResultResponse resultResponse = requestAuthResult(
                context.accessToken(),
                niceCallbackCommand.webTransactionId(),
                context.niceTransactionId(),
                context.requestNo());

        // PBKDF2 salt = NICE의 transaction_id
        NiceSymmetricSalt salt = niceHttpCredentialFacade.deriveSymmetricSalt(
                context.ticket(), context.niceTransactionId(), context.iterators());

        niceVerificationService.verify(salt, resultResponse.encData(), resultResponse.integrityValue());

        String decryptedPayload = niceAesGcmCryptoService.decrypt(resultResponse.encData(), salt);

        try {
            NiceVerifiedUser verifiedUser = objectMapper.readValue(decryptedPayload, NiceVerifiedUser.class);
            verifyResultCode(verifiedUser);
            return verifiedUser;
        } catch (JsonProcessingException e) {
            throw new AccountAuthenticationValidationFailedException();
        }
    }

    private void verifyResultCode(final NiceVerifiedUser verifiedUser) {
        final String resultCode = verifiedUser.resultCode();

        if (resultCode != null && !RESULT_CODE_SUCCESS.equals(resultCode)) {
            log.warn("[NICE] 본인인증 결과 코드가 성공이 아니다 resultcode={}", resultCode);
            throw new AccountAuthenticationValidationFailedException();
        }
    }

    private NiceAuthResultResponse requestAuthResult(
            String accessToken, String webTransactionId, String niceTransactionId, String requestNo) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, niceProperties.generateBearerAuthHeader(accessToken));
        headers.setContentType(MediaType.APPLICATION_JSON);

        NiceAuthResultRequest requestBody = NiceAuthResultRequest.builder()
                .webTransactionId(webTransactionId)
                .transactionId(niceTransactionId)
                .requestNo(requestNo)
                .build();

        ResponseEntity<String> response = restTemplate.exchange(
                niceProperties.authResultRequestUrl(),
                HttpMethod.POST,
                new HttpEntity<>(requestBody, headers),
                new ParameterizedTypeReference<>() {});

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new AccountNiceApiInternalServerException();
        }

        try {
            return objectMapper.readValue(response.getBody(), NiceAuthResultResponse.class);
        } catch (JsonProcessingException e) {
            throw new AccountNiceApiInternalServerException();
        }
    }
}
