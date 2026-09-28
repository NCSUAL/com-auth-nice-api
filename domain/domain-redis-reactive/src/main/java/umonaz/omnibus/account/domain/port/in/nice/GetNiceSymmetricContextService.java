package umonaz.omnibus.account.domain.port.in.nice;

import reactor.core.publisher.Mono;
import umonaz.omnibus.account.domain.model.symmetric.NiceSymmetricContext;

public interface GetNiceSymmetricContextService {

    public Mono<NiceSymmetricContext> execute(String transactionId);
}
