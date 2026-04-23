package org.jedi_bachelor.bookstatistic.configuration;

import org.jedi_bachelor.bookstatistic.internalinteraction.InteractionClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
public class ClientConfiguration {
    @Value("${external-url:book}")
    private String bookBaseUrl;

    @Value("${external-url:analyze}")
    private String analyzerBaseUrl;

    @Bean
    public InteractionClient bookInteractionClient() {
        return new InteractionClient(this.bookBaseUrl, HttpHeaders.EMPTY);
    }

    @Bean
    public InteractionClient analyzerInteractionClient() {
        return new InteractionClient(this.analyzerBaseUrl, HttpHeaders.EMPTY);
    }
}
