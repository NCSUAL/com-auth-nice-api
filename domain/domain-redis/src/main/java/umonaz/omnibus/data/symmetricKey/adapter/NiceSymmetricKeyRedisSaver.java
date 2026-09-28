package umonaz.omnibus.data.symmetricKey.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.RedisConnectionFailureException;
import umonaz.omnibus.account.domain.exception.AccountRedisConnectionFailedException;
import umonaz.omnibus.account.domain.model.symmetric.NiceSymmetricKey;
import umonaz.omnibus.account.domain.port.NiceSymmetricKeySaver;
import umonaz.omnibus.data.annotation.Saver;
import umonaz.omnibus.data.symmetricKey.repository.NiceSymmetricKeyRepository;

@Saver
@ConditionalOnProperty(prefix = "omnibus-redis-local", name = "enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class NiceSymmetricKeyRedisSaver implements NiceSymmetricKeySaver {

    private final NiceSymmetricKeyRepository reativeNiceSymmetricKeyRepository;

    @Override
    public void execute(NiceSymmetricKey niceSymmetricKey) {
        try {
            reativeNiceSymmetricKeyRepository.save(niceSymmetricKey);
        } catch (RedisConnectionFailureException e) {
            throw new AccountRedisConnectionFailedException();
        }
    }
}
