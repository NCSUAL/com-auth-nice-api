package umonaz.omnibus.service.nice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import umonaz.omnibus.account.domain.credential.NiceCredential;
import umonaz.omnibus.account.domain.credential.NiceSymmetricSalt;
import umonaz.omnibus.account.domain.exception.AccountNiceApiInternalServerException;
import umonaz.omnibus.account.domain.exception.AccountPayloadResponseFailedException;
import umonaz.omnibus.account.domain.model.symmetric.NiceSymmetricKey;
import umonaz.omnibus.account.domain.port.in.key.NiceSymmetricKeyService;
import umonaz.omnibus.account.domain.service.NiceCredentialIssuer;
import umonaz.omnibus.client.properties.NiceProperties;
import umonaz.omnibus.client.request.NiceAuthUrlRequest;
import umonaz.omnibus.client.response.NiceAuthUrlResponse;
import umonaz.omnibus.client.response.NiceTokenResponse;

@Service
public class NiceHttpCredentialFacade implements NiceCredentialIssuer {

    private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int DERIVED_KEY_BITS = 512;

    private final NiceSymmetricKeyService reativeNiceSymmetricKeyService;
    private final WebClient webClient;
    private final NiceProperties niceProperties;
    private final ObjectMapper objectMapper;

    public NiceHttpCredentialFacade(
            WebClient webClient, NiceProperties niceProperties, NiceSymmetricKeyService niceSymmetricKeyService) {
        this.webClient = webClient;
        this.niceProperties = niceProperties;
        this.reativeNiceSymmetricKeyService = niceSymmetricKeyService;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Mono<NiceCredential> issue() {
        String requestNo = NiceProperties.generateRequestNo();
        String dynamicReturnUrl = niceProperties.returnUrl() + "?request_no=" + requestNo;

        return requestAccessToken(requestNo)
                .flatMap(tokenResponse -> requestAuthUrl(tokenResponse.accessToken(), requestNo, dynamicReturnUrl)
                        .flatMap(authUrlResponse -> reativeNiceSymmetricKeyService
                                .createSymmetricKey(NiceSymmetricKey.of(requestNo, tokenResponse, authUrlResponse))
                                .thenReturn(NiceCredential.builder()
                                        .transactionId(requestNo)
                                        .authUrl(authUrlResponse.authUrl())
                                        .build())));
    }

    private Mono<NiceTokenResponse> requestAccessToken(String requestNo) {
        java.util.Map<String, String> requestBody =
                java.util.Map.of("grant_type", "client_credentials", "request_no", requestNo);

        return webClient
                .post()
                .uri(niceProperties.tokenRequestUrl())
                .header(HttpHeaders.AUTHORIZATION, niceProperties.generateBasicAuthHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .map(body -> parse(body, NiceTokenResponse.class))
                .flatMap(token -> {
                    if (token.isSuccess()) {
                        return Mono.error(new AccountNiceApiInternalServerException());
                    }
                    return Mono.just(token);
                })
                .onErrorMap(e -> new AccountNiceApiInternalServerException());
    }

    private Mono<NiceAuthUrlResponse> requestAuthUrl(String accessToken, String requestNo, String returnUrl) {
        NiceAuthUrlRequest requestBody = niceProperties.buildAuthUrlRequest(requestNo, returnUrl);
        return webClient
                .post()
                .uri(niceProperties.authUrlRequestUrl())
                .header(HttpHeaders.AUTHORIZATION, niceProperties.generateBearerAuthHeader(accessToken))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .map(body -> parse(body, NiceAuthUrlResponse.class))
                .onErrorMap(e -> new AccountNiceApiInternalServerException());
    }

    public NiceSymmetricSalt deriveSymmetricSalt(String ticket, String niceTransactionId, int iterators) {
        try {
            KeySpec spec = new PBEKeySpec(
                    ticket.toCharArray(),
                    niceTransactionId.getBytes(StandardCharsets.UTF_8),
                    iterators,
                    DERIVED_KEY_BITS);
            byte[] derived = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM)
                    .generateSecret(spec)
                    .getEncoded();
            String encodedKey = Base64.getUrlEncoder().withoutPadding().encodeToString(derived);
            return NiceSymmetricSalt.builder()
                    .key(encodedKey.substring(0, 32))
                    .hmacKey(encodedKey.substring(48, 80))
                    .build();
        } catch (Exception e) {
            throw new AccountNiceApiInternalServerException();
        }
    }

    private <T> T parse(String json, Class<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new AccountPayloadResponseFailedException();
        }
    }
}
