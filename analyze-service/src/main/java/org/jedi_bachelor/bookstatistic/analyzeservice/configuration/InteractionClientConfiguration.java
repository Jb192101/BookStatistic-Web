package org.jedi_bachelor.bookstatistic.analyzeservice.configuration;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.bookstatistic.commonslib.internalinteraction.InteractionClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@Configuration
@RequiredArgsConstructor
public class InteractionClientConfiguration {
    private final InteractionPathsConfiguration interactionPathsConfiguration;

    @Bean("accountInteractionClient")
    public InteractionClient accountInteractionClient() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new InteractionClient(this.interactionPathsConfiguration.getAccountBaseUrl(), headers);
    }

    @Bean("bookInteractionClient")
    public InteractionClient bookInteractionClient() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new InteractionClient(this.interactionPathsConfiguration.getBookBaseUrl(), headers);
    }

    @Bean("responseInteractionClient")
    public InteractionClient responseInteractionClient() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new InteractionClient(this.interactionPathsConfiguration.getResponseBaseUrl(), headers);
    }
}
