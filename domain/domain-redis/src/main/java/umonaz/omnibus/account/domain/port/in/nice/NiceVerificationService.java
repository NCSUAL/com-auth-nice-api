package umonaz.omnibus.account.domain.port.in.nice;

import umonaz.omnibus.account.domain.credential.NiceSymmetricSalt;
import umonaz.omnibus.account.domain.model.symmetric.NiceSymmetricContext;

public interface NiceVerificationService {

    void verify(NiceSymmetricSalt salt, String encData, String integrityValue);

    void verify(NiceSymmetricContext niceSymmetricContext, String requestNo);
}
