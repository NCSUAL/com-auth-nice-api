package umonaz.omnibus.data.symmetricKey.reactive.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import umonaz.omnibus.account.domain.exception.AccountFailedSaveException;
import umonaz.omnibus.account.domain.exception.AccountNotFoundException;
import umonaz.omnibus.account.domain.model.symmetric.NiceSymmetricContext;
import umonaz.omnibus.account.domain.model.symmetric.NiceSymmetricKey;

public interface NiceSymmetricKeyRepository {

    Mono<Void> save(NiceSymmetricKey niceSymmetricKey);

    Mono<NiceSymmetricContext> findById(final String transactionId);

    // portable service abstraction
    @Repository
    @RequiredArgsConstructor
    class ReactiveNiceSymmetricKeyRepositoryImpl implements NiceSymmetricKeyRepository {

        private final ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;

        @Override
        public Mono<Void> save(NiceSymmetricKey niceSymmetricKey) {
            return reactiveRedisTemplate
                    .opsForValue()
                    .set(
                            niceSymmetricKey.getId(),
                            niceSymmetricKey.getNiceSymmetricContext(),
                            NiceSymmetricKey.getTtl())
                    .doOnNext(status -> {
                        if (!status) {
                            throw new AccountFailedSaveException();
                        }
                    })
                    .then();
        }

        @Override
        public Mono<NiceSymmetricContext> findById(String transactionId) {
            return reactiveRedisTemplate
                    .opsForValue()
                    .get(NiceSymmetricKey.getId(transactionId))
                    .switchIfEmpty(Mono.error(AccountNotFoundException::new))
                    .cast(NiceSymmetricContext.class);
        }
    }
}
