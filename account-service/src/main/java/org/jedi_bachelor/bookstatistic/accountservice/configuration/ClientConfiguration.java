package org.jedi_bachelor.bookstatistic.accountservice.configuration;

import org.jedi_bachelor.bookstatistic.commonslib.internalinteraction.InteractionClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@Configuration
public class ClientConfiguration {
    @Autowired
    private InteractionPathsConfiguration interactionPathsConfiguration;

    @Bean
    public InteractionClient analyzerInteractionClient() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new InteractionClient(this.interactionPathsConfiguration.getAnalyzeBasePath(), headers);
    }

    @Bean
    public InteractionClient notificationInteractionClient() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new InteractionClient(this.interactionPathsConfiguration.getNotificationBasePath(), headers);
    }
}
