package umonaz.omnibus.account.domain.model.symmetric;

import java.time.Duration;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import umonaz.omnibus.client.response.NiceAuthUrlResponse;
import umonaz.omnibus.client.response.NiceTokenResponse;

@RedisHash(value = "nice:symmetrickey", timeToLive = 600L)
@Builder
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class NiceSymmetricKey {

    private static final @Getter Duration ttl = Duration.ofSeconds(600L);

    private static final String redisHashKey = "nice:symmetrickey:";

    @Id
    private String transactionId;

    private NiceSymmetricContext niceSymmetricContext;

    public static String getId(final String transactionId) {
        return redisHashKey.concat(transactionId);
    }

    public String getId() {
        return redisHashKey.concat(transactionId);
    }

    public static NiceSymmetricKey of(
            String requestNo, NiceTokenResponse tokenResponse, NiceAuthUrlResponse authUrlResponse) {
        return NiceSymmetricKey.builder()
                .transactionId(requestNo)
                .niceSymmetricContext(NiceSymmetricContext.of(
                        tokenResponse.ticket(),
                        tokenResponse.iterators(),
                        requestNo,
                        tokenResponse.accessToken(),
                        authUrlResponse.transactionId()))
                .build();
    }
}
