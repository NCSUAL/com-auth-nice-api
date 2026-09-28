package umonaz.omnibus.data.config;

import io.lettuce.core.ClientOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import umonaz.omnibus.data.properties.RedisProperties;

@Configuration
@Import(RedisPropertiesConfig.class)
@RequiredArgsConstructor
@Profile("reactive")
@Slf4j
public class ReactiveRedisConfig {

    private final RedisProperties redisProperties;

    @Bean
    @ConditionalOnMissingBean(ReactiveRedisConnectionFactory.class)
    ReactiveRedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(redisProperties.host());
        configuration.setPort(redisProperties.port());
        configuration.setUsername(redisProperties.userName());
        configuration.setPassword(redisProperties.password());

        log.info("[ACCOUNT: Redis 정보] username: {}, url: {}", redisProperties.userName(), redisProperties.host());

        LettuceClientConfiguration clientConfiguration = LettuceClientConfiguration.builder()
                .clientOptions(ClientOptions.builder().autoReconnect(true).build())
                .build();
        return new LettuceConnectionFactory(configuration, clientConfiguration);
    }

    @Bean
    RedisSerializationContext<String, Object> redisSerializationContext() {
        return RedisSerializationContext.<String, Object>newSerializationContext(new StringRedisSerializer())
                .value(new GenericJackson2JsonRedisSerializer())
                .hashKey(new StringRedisSerializer())
                .hashValue(new GenericJackson2JsonRedisSerializer())
                .build();
    }

    @Bean
    @ConditionalOnMissingBean(ReactiveRedisTemplate.class)
    ReactiveRedisTemplate<String, Object> reactiveRedisTemplate() {
        ReactiveRedisTemplate<String, Object> reactiveRedisTemplate =
                new ReactiveRedisTemplate<>(redisConnectionFactory(), redisSerializationContext());
        reactiveRedisTemplate
                .execute(connection -> connection.ping())
                .doOnNext(pong -> log.info("[ACCOUNT: Redis 연결 성공: {}]", pong))
                .doOnError(error -> log.error("[ACCOUNT: Redis 연결 실패]: ", error))
                .subscribe();
        return reactiveRedisTemplate;
    }
}
