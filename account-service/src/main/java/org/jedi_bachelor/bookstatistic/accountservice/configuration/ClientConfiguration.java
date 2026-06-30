package org.jedi_bachelor.bookstatistic.accountservice.configuration;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.bookstatistic.commonslib.internalinteraction.InteractionClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@Configuration
@RequiredArgsConstructor
public class ClientConfiguration {
    private final InteractionPathsConfiguration interactionPathsConfiguration;

    @Bean
    public InteractionClient notificationInteractionClient() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new InteractionClient(this.interactionPathsConfiguration.getNotificationBasePath(), headers);
    }
}
