package umonaz.omnibus.client.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ApiClientConfig {

    @Bean
    @ConditionalOnMissingBean(value = WebClient.class)
    WebClient webClient(WebClient.Builder builder) {
        return builder.build();
    }
}
