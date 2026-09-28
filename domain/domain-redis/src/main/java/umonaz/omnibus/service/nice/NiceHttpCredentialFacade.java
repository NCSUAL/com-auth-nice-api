package umonaz.omnibus.service.nice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
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
@Slf4j
public class NiceHttpCredentialFacade implements NiceCredentialIssuer {

    private static final String DEV_LANG_HEADER = "X-Intc-DevLang";
    private static final String DEV_LANG_VALUE = "Linux/Java";

    private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int DERIVED_KEY_BITS = 512;

    private final NiceSymmetricKeyService niceSymmetricKeyService;
    private final RestTemplate restTemplate;
    private final NiceProperties niceProperties;
    private final ObjectMapper objectMapper;

    public NiceHttpCredentialFacade(
            RestTemplate restTemplate,
            NiceProperties niceProperties,
            NiceSymmetricKeyService niceSymmetricKeyService,
            ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.niceProperties = niceProperties;
        this.niceSymmetricKeyService = niceSymmetricKeyService;
        this.objectMapper = objectMapper;
    }

    @Override
    public NiceCredential issue() {
        String requestNo = NiceProperties.generateRequestNo();
        String dynamicReturnUrl = niceProperties.returnUrl() + "?request_no=" + requestNo;

        NiceTokenResponse tokenResponse = requestAccessToken(requestNo);
        NiceAuthUrlResponse authUrlResponse = requestAuthUrl(tokenResponse.accessToken(), requestNo, dynamicReturnUrl);

        niceSymmetricKeyService.createSymmetricKey(NiceSymmetricKey.of(requestNo, tokenResponse, authUrlResponse));

        return NiceCredential.builder()
                .transactionId(requestNo)
                .authUrl(authUrlResponse.authUrl())
                .build();
    }

    private NiceTokenResponse requestAccessToken(String requestNo) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, niceProperties.generateBasicAuthHeader());
        headers.set(DEV_LANG_HEADER, DEV_LANG_VALUE);
        headers.setContentType(MediaType.APPLICATION_JSON);

        java.util.Map<String, String> body =
                java.util.Map.of("grant_type", "client_credentials", "request_no", requestNo);

        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(
                    niceProperties.tokenRequestUrl(),
                    HttpMethod.POST,
                    new HttpEntity<>(body, headers),
                    new ParameterizedTypeReference<>() {});
        } catch (Exception e) {
            log.error("[NICE] 접근토큰 요청 실패 - {}: {}", e.getClass().getSimpleName(), e.getMessage());
            throw new AccountNiceApiInternalServerException();
        }

        NiceTokenResponse tokenResponse = parse(response.getBody(), NiceTokenResponse.class);
        if (tokenResponse.isSuccess()) {
            log.error(
                    "[NICE] 접근토큰 발급 실패 result_code={} message={}",
                    tokenResponse.resultCode(),
                    tokenResponse.resultMessage());
            throw new AccountNiceApiInternalServerException();
        }

        return tokenResponse;
    }

    private NiceAuthUrlResponse requestAuthUrl(String accessToken, String requestNo, String returnUrl) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, niceProperties.generateBearerAuthHeader(accessToken));
        headers.set(DEV_LANG_HEADER, DEV_LANG_VALUE);
        headers.setContentType(MediaType.APPLICATION_JSON);

        NiceAuthUrlRequest requestBody = niceProperties.buildAuthUrlRequest(requestNo, returnUrl);

        ResponseEntity<String> response = restTemplate.exchange(
                niceProperties.authUrlRequestUrl(),
                HttpMethod.POST,
                new HttpEntity<>(requestBody, headers),
                new ParameterizedTypeReference<>() {});

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new AccountNiceApiInternalServerException();
        }

        return parse(response.getBody(), NiceAuthUrlResponse.class);
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
