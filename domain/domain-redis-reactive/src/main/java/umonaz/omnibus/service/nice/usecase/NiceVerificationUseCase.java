package umonaz.omnibus.service.nice.usecase;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
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
        if (niceHmacIntegrityService.verifyHmac(VerifyHmac.of(integrityValue, encData, salt.hmacKey()))) {
            return;
        }
        throw new AccountAuthenticationValidationFailedException();
    }

    @Override
    public Mono<Void> verify(NiceSymmetricContext niceSymmetricContext, final String requestNo) {
        if (niceSymmetricContext.requestNo().equals(requestNo)) {
            return Mono.empty();
        }
        return Mono.error(new AccountAuthenticationValidationFailedException());
    }
}
