package umonaz.omnibus.client.properties;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import org.springframework.boot.context.properties.ConfigurationProperties;
import umonaz.omnibus.client.request.NiceAuthUrlRequest;

@ConfigurationProperties(prefix = "omnibus.nice")
public record NiceProperties(
        String clientId,
        String clientSecret,
        String tokenRequestUrl,
        String authUrlRequestUrl,
        String authResultRequestUrl,
        String returnUrl,
        String closeUrl,
        List<String> svcTypes,
        String methodType) {

    public static String generateRequestNo() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 30);
    }

    public String generateBasicAuthHeader() {
        String credentials = clientId + ":" + clientSecret;
        return "Basic "
                + Base64.getUrlEncoder().withoutPadding().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    }

    public String generateBearerAuthHeader(final String accessToken) {
        return "Bearer " + accessToken;
    }

    public NiceAuthUrlRequest buildAuthUrlRequest(String requestNo, String dynamicReturnUrl) {
        return NiceAuthUrlRequest.builder()
                .requestNo(requestNo)
                .returnUrl(dynamicReturnUrl)
                .closeUrl(closeUrl)
                .svcTypes(svcTypes)
                .methodType(methodType)
                .build();
    }
}
