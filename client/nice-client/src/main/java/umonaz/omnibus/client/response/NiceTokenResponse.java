package umonaz.omnibus.client.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NiceTokenResponse(
        @JsonProperty("result_code") String resultCode,
        @JsonProperty("result_message") String resultMessage,
        @JsonProperty("request_no") String requestNo,
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("expires_in") long expiresIn,
        @JsonProperty("token_type") String tokenType,
        int iterators,
        String ticket) {

    public boolean isSuccess() {
        return !"0000".equals(resultCode);
    }
}
