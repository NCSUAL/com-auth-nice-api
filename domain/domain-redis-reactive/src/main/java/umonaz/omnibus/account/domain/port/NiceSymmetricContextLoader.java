package umonaz.omnibus.account.domain.port;

import reactor.core.publisher.Mono;
import umonaz.omnibus.account.domain.model.symmetric.NiceSymmetricContext;

public interface NiceSymmetricContextLoader {

    public Mono<NiceSymmetricContext> loadById(final String transactionId);
}
