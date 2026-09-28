package umonaz.omnibus.client.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "omnibus-pepper.nice.identity")
public record NicePepperProperties(String requestUrl, String ciPepper, String diPepper) {}
