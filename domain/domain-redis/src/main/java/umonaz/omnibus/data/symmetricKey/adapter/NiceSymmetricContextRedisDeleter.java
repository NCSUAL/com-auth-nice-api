package umonaz.omnibus.data.symmetricKey.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.RedisConnectionFailureException;
import umonaz.omnibus.account.domain.exception.AccountRedisConnectionFailedException;
import umonaz.omnibus.account.domain.port.NiceSymmetricContextDeleter;
import umonaz.omnibus.data.annotation.Deleter;
import umonaz.omnibus.data.symmetricKey.repository.NiceSymmetricKeyRepository;

@Deleter
@ConditionalOnProperty(prefix = "omnibus-redis-local", name = "enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class NiceSymmetricContextRedisDeleter implements NiceSymmetricContextDeleter {

    private final NiceSymmetricKeyRepository niceSymmetricKeyRepository;

    @Override
    public void deleteById(final String transactionId) {
        try {
            niceSymmetricKeyRepository.deleteById(transactionId);
        } catch (RedisConnectionFailureException e) {
            throw new AccountRedisConnectionFailedException();
        }
    }
}
