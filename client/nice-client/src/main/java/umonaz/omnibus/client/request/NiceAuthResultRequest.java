package umonaz.omnibus.client.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record NiceAuthResultRequest(
        @JsonProperty("web_transaction_id") String webTransactionId,
        @JsonProperty("transaction_id") String transactionId,
        @JsonProperty("request_no") String requestNo) {}
