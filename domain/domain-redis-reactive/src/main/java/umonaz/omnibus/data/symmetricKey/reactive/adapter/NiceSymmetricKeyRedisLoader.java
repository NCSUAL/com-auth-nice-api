package umonaz.omnibus.data.symmetricKey.reactive.adapter;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import umonaz.omnibus.account.domain.model.symmetric.NiceSymmetricContext;
import umonaz.omnibus.account.domain.port.NiceSymmetricContextLoader;
import umonaz.omnibus.data.annotation.Loader;
import umonaz.omnibus.data.symmetricKey.reactive.repository.NiceSymmetricKeyRepository;

@Loader
@RequiredArgsConstructor
public class NiceSymmetricKeyRedisLoader implements NiceSymmetricContextLoader {

    private final NiceSymmetricKeyRepository reativeNiceSymmetricKeyRepository;

    @Override
    public Mono<NiceSymmetricContext> loadById(String transactionId) {
        return reativeNiceSymmetricKeyRepository.findById(transactionId);
    }
}
