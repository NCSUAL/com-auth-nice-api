package umonaz.omnibus.data.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "omnibus-redis-local")
public record RedisProperties(String host, Integer port, String userName, String password) {}
