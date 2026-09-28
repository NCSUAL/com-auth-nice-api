package umonaz.omnibus.account.domain.port.in.nice;

import reactor.core.publisher.Mono;
import umonaz.omnibus.account.domain.credential.NiceSymmetricSalt;
import umonaz.omnibus.account.domain.model.symmetric.NiceSymmetricContext;

public interface NiceVerificationService {

    void verify(NiceSymmetricSalt salt, String encData, String integrityValue);

    Mono<Void> verify(NiceSymmetricContext niceSymmetricContext, String requestNo);
}
