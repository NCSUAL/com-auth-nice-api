package umonaz.omnibus.client.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NiceAuthUrlResponse(
        @JsonProperty("auth_url") String authUrl,
        @JsonProperty("transaction_id") String transactionId,
        @JsonProperty("request_no") String requestNo) {}
