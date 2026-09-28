package umonaz.omnibus.account.domain.credential;

import lombok.Builder;
import umonaz.omnibus.client.response.NiceAuthUrlResponse;
import umonaz.omnibus.client.response.NiceTokenResponse;

@Builder
public record NiceCredentialMaterial(NiceTokenResponse niceTokenResponse, NiceAuthUrlResponse niceAuthUrlResponse) {

    public static NiceCredentialMaterial of(
            NiceTokenResponse niceTokenResponse, NiceAuthUrlResponse niceAuthUrlResponse) {
        return NiceCredentialMaterial.builder()
                .niceTokenResponse(niceTokenResponse)
                .niceAuthUrlResponse(niceAuthUrlResponse)
                .build();
    }
}
