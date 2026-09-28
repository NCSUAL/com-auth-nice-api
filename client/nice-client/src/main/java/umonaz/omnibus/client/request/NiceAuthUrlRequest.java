package umonaz.omnibus.client.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record NiceAuthUrlRequest(
        @JsonProperty("request_no") String requestNo,
        @JsonProperty("return_url") String returnUrl,
        @JsonProperty("close_url") String closeUrl,
        @JsonProperty("svc_types") List<String> svcTypes,
        @JsonProperty("method_type") String methodType,
        @JsonProperty("exp_mods") List<String> expMods) {}
