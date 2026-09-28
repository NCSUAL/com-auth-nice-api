package umonaz.omnibus.service.nice.usecase;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import umonaz.omnibus.account.domain.model.symmetric.NiceSymmetricContext;
import umonaz.omnibus.account.domain.port.NiceSymmetricContextLoader;
import umonaz.omnibus.account.domain.port.in.nice.GetNiceSymmetricContextService;
import umonaz.omnibus.service.annotation.UseCase;

@UseCase
@RequiredArgsConstructor
public class GetNiceSymmetricContextUseCase implements GetNiceSymmetricContextService {

    private final NiceSymmetricContextLoader reactiveNiceSymmetricContextLoader;

    @Override
    public Mono<NiceSymmetricContext> execute(String transactionId) {
        return reactiveNiceSymmetricContextLoader.loadById(transactionId);
    }
}
