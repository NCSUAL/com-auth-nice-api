package umonaz.omnibus.data.symmetricKey.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.RedisConnectionFailureException;
import umonaz.omnibus.account.domain.exception.AccountRedisConnectionFailedException;
import umonaz.omnibus.account.domain.model.symmetric.NiceSymmetricContext;
import umonaz.omnibus.account.domain.port.NiceSymmetricContextLoader;
import umonaz.omnibus.auth.exception.AuthTokenNotFoundException;
import umonaz.omnibus.data.annotation.Loader;
import umonaz.omnibus.data.symmetricKey.repository.NiceSymmetricKeyRepository;

@Loader
@ConditionalOnProperty(prefix = "omnibus-redis-local", name = "enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class NiceSymmetricContextRedisLoader implements NiceSymmetricContextLoader {

    private final NiceSymmetricKeyRepository NiceSymmetricKeyRepository;

    @Override
    public NiceSymmetricContext loadById(String transactionId) {
        try {
            return NiceSymmetricKeyRepository.findById(transactionId)
                    .orElseThrow(AuthTokenNotFoundException::new)
                    .getNiceSymmetricContext();
        } catch (RedisConnectionFailureException e) {
            throw new AccountRedisConnectionFailedException();
        }
    }
}
