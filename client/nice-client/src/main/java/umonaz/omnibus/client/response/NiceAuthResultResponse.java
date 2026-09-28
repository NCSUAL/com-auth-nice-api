package umonaz.omnibus.client.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NiceAuthResultResponse(
        @JsonProperty("enc_data") String encData, @JsonProperty("integrity_value") String integrityValue) {}
