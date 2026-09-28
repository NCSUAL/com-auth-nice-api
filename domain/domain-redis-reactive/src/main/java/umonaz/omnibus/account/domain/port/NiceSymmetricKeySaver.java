package umonaz.omnibus.account.domain.port;

import reactor.core.publisher.Mono;
import umonaz.omnibus.account.domain.model.symmetric.NiceSymmetricKey;

public interface NiceSymmetricKeySaver {

    public Mono<Void> execute(final NiceSymmetricKey niceSymmetricKey);
}
