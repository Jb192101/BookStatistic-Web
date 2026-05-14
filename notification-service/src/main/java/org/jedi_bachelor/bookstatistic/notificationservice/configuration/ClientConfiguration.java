package org.jedi_bachelor.bookstatistic.notificationservice.configuration;

import org.jedi_bachelor.bookstatistic.commonslib.internalinteraction.InteractionClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfiguration {
    @Value("${external-url.account.base-url}")
    private String accountBaseUrl;

    @Bean
    public InteractionClient accountClient() {
        return new InteractionClient(this.accountBaseUrl, null, "account-service");
    }
}
