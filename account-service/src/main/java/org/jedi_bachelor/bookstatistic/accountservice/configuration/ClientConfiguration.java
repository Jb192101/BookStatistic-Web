package org.jedi_bachelor.bookstatistic.accountservice.configuration;

import org.jedi_bachelor.bookstatistic.commonslib.internalinteraction.InteractionClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@Configuration
public class ClientConfiguration {
    @Value("${external-urls.analyze}")
    private String analyzerBaseUrl;

    @Value("${external-urls.notification}")
    private String notificationBaseUrl;

    @Bean
    public InteractionClient analyzerInteractionClient() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new InteractionClient(analyzerBaseUrl, headers);
    }

    @Bean
    public InteractionClient notificationInteractionClient() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new InteractionClient(notificationBaseUrl, headers);
    }
}
