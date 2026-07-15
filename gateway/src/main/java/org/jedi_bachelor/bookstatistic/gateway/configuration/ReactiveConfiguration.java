package org.jedi_bachelor.bookstatistic.gateway.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ReactiveConfiguration {
    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }
}
