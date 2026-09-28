package umonaz.omnibus.service.symmetric;

import lombok.RequiredArgsConstructor;
import umonaz.omnibus.account.domain.model.symmetric.NiceSymmetricKey;
import umonaz.omnibus.account.domain.port.NiceSymmetricKeySaver;
import umonaz.omnibus.account.domain.port.in.key.NiceSymmetricKeyService;
import umonaz.omnibus.service.annotation.UseCase;

@UseCase
@RequiredArgsConstructor
public class NiceSymmetricKeyUseCase implements NiceSymmetricKeyService {

    private final NiceSymmetricKeySaver niceSymmetricKeySaver;

    @Override
    public void createSymmetricKey(NiceSymmetricKey niceSymmetricKey) {
        niceSymmetricKeySaver.execute(niceSymmetricKey);
    }
}
