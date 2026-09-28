package umonaz.omnibus.service.nice.usecase;

import lombok.RequiredArgsConstructor;
import umonaz.omnibus.account.domain.credential.NiceSymmetricSalt;
import umonaz.omnibus.account.domain.exception.AccountAuthenticationValidationFailedException;
import umonaz.omnibus.account.domain.model.symmetric.NiceSymmetricContext;
import umonaz.omnibus.account.domain.port.in.HmacIntegrityService.VerifyHmac;
import umonaz.omnibus.account.domain.port.in.nice.NiceVerificationService;
import umonaz.omnibus.service.annotation.UseCase;
import umonaz.omnibus.service.crypto.NiceHmacIntegrityService;

@UseCase
@RequiredArgsConstructor
public class NiceVerificationUseCase implements NiceVerificationService {

    private final NiceHmacIntegrityService niceHmacIntegrityService;

    @Override
    public void verify(NiceSymmetricSalt salt, String encData, String integrityValue) {
        String hmacKey = salt.hmacKey();
        if (niceHmacIntegrityService.verifyHmac(VerifyHmac.of(integrityValue, encData, hmacKey))) {
            return;
        }
        throw new AccountAuthenticationValidationFailedException();
    }

    @Override
    public void verify(NiceSymmetricContext niceSymmetricContext, final String requestNo) {
        if (niceSymmetricContext.requestNo().equals(requestNo)) {
            return;
        }
        throw new AccountAuthenticationValidationFailedException();
    }
}
