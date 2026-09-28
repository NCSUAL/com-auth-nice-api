package umonaz.omnibus.client.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import umonaz.omnibus.client.properties.NicePepperProperties;
import umonaz.omnibus.client.properties.NiceProperties;

@Configuration
@EnableConfigurationProperties({NiceProperties.class, NicePepperProperties.class})
public class PropertiesConfig {}
