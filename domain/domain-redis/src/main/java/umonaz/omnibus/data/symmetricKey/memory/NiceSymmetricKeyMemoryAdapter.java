package umonaz.omnibus.data.symmetricKey.memory;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import umonaz.omnibus.account.domain.model.symmetric.NiceSymmetricContext;
import umonaz.omnibus.account.domain.model.symmetric.NiceSymmetricKey;
import umonaz.omnibus.account.domain.port.NiceSymmetricContextDeleter;
import umonaz.omnibus.account.domain.port.NiceSymmetricContextLoader;
import umonaz.omnibus.account.domain.port.NiceSymmetricKeySaver;
import umonaz.omnibus.auth.exception.AuthTokenNotFoundException;

@Repository
@ConditionalOnProperty(prefix = "omnibus-redis-local", name = "enabled", havingValue = "false")
public class NiceSymmetricKeyMemoryAdapter
        implements NiceSymmetricKeySaver, NiceSymmetricContextLoader, NiceSymmetricContextDeleter {

    private final Map<String, Entry> store = new ConcurrentHashMap<>();

    @Override
    public void execute(final NiceSymmetricKey niceSymmetricKey) {
        Instant now = Instant.now();
        store.values().removeIf(entry -> entry.isExpired(now));
        store.put(
                niceSymmetricKey.getTransactionId(),
                new Entry(niceSymmetricKey.getNiceSymmetricContext(), now.plus(NiceSymmetricKey.getTtl())));
    }

    @Override
    public NiceSymmetricContext loadById(final String transactionId) {
        Entry entry = store.get(transactionId);
        if (entry == null || entry.isExpired(Instant.now())) {
            throw new AuthTokenNotFoundException();
        }
        return entry.context();
    }

    @Override
    public void deleteById(final String transactionId) {
        store.remove(transactionId);
    }

    private record Entry(NiceSymmetricContext context, Instant expiresAt) {

        boolean isExpired(final Instant now) {
            return now.isAfter(expiresAt);
        }
    }
}
