package umonaz.omnibus.account.domain.port.in.nice;

import reactor.core.publisher.Mono;
import umonaz.omnibus.account.domain.model.symmetric.NiceSymmetricKey;

public interface ReactiveNiceSymmetricKeyService {

    public Mono<Void> createSymmetricKey(NiceSymmetricKey niceSymmetricKey);
}
