package umonaz.omnibus.service.symmetric;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import umonaz.omnibus.account.domain.model.symmetric.NiceSymmetricKey;
import umonaz.omnibus.account.domain.port.NiceSymmetricKeySaver;
import umonaz.omnibus.account.domain.port.in.key.NiceSymmetricKeyService;
import umonaz.omnibus.service.annotation.UseCase;

@UseCase
@RequiredArgsConstructor
public class NiceSymmetricKeyUseCase implements NiceSymmetricKeyService {

    private final NiceSymmetricKeySaver reactiveNiceSymmetricKeySaver;

    @Override
    public Mono<Void> createSymmetricKey(NiceSymmetricKey niceSymmetricKey) {
        return reactiveNiceSymmetricKeySaver.execute(niceSymmetricKey);
    }
}
