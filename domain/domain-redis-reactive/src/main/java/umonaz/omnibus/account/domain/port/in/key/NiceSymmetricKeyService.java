package umonaz.omnibus.account.domain.port.in.key;

import reactor.core.publisher.Mono;
import umonaz.omnibus.account.domain.model.symmetric.NiceSymmetricKey;

public interface NiceSymmetricKeyService {

    public Mono<Void> createSymmetricKey(NiceSymmetricKey niceSymmetricKey);
}
