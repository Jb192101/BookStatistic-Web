package org.jedi_bachelor.bookstatistic.notificationservice.configuration;

import org.jedi_bachelor.bookstatistic.commonslib.internalinteraction.InteractionClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class ClientConfiguration {
    private InteractionPathsConfiguration interactionPathsConfiguration;

    @Bean
    @Order(-1)
    public InteractionClient accountClient() {
        return new InteractionClient(this.interactionPathsConfiguration.getAccountBasePath(), null);
    }
}
