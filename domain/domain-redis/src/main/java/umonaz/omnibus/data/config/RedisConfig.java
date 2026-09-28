package umonaz.omnibus.data.config;

import io.lettuce.core.ClientOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import umonaz.omnibus.data.properties.RedisProperties;

@Configuration
@ConditionalOnProperty(prefix = "omnibus-redis-local", name = "enabled", havingValue = "true", matchIfMissing = true)
@Import(RedisPropertiesConfig.class)
@EnableRedisRepositories(basePackages = "umonaz.omnibus.data")
@RequiredArgsConstructor
@Slf4j
public class RedisConfig {

    private final RedisProperties redisProperties;

    @Bean
    @ConditionalOnMissingBean(LettuceConnectionFactory.class)
    LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(redisProperties.host());
        configuration.setPort(redisProperties.port());
        configuration.setUsername(redisProperties.userName());
        configuration.setPassword(redisProperties.password());

        log.info("[Redis 정보] username: {}, url: {}", redisProperties.userName(), redisProperties.host());

        LettuceClientConfiguration clientConfiguration = LettuceClientConfiguration.builder()
                .clientOptions(ClientOptions.builder().autoReconnect(true).build())
                .build();
        return new LettuceConnectionFactory(configuration, clientConfiguration);
    }
}
