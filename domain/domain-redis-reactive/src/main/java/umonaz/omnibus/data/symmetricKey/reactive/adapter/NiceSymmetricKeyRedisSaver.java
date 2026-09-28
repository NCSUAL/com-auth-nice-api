package umonaz.omnibus.data.symmetricKey.reactive.adapter;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import umonaz.omnibus.account.domain.model.symmetric.NiceSymmetricKey;
import umonaz.omnibus.account.domain.port.NiceSymmetricKeySaver;
import umonaz.omnibus.data.annotation.Saver;
import umonaz.omnibus.data.symmetricKey.reactive.repository.NiceSymmetricKeyRepository;

@Saver
@RequiredArgsConstructor
public class NiceSymmetricKeyRedisSaver implements NiceSymmetricKeySaver {

    private final NiceSymmetricKeyRepository reativeNiceSymmetricKeyRepository;

    @Override
    public Mono<Void> execute(NiceSymmetricKey niceSymmetricKey) {
        return reativeNiceSymmetricKeyRepository.save(niceSymmetricKey);
    }
}
