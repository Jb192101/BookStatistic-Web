package org.jedi_bachelor.bookstatistic.commonslib.configuration;

import org.jedi_bachelor.bookstatistic.commonslib.utils.filterchains.UserContextInterceptor;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestConfiguration {
    @LoadBalanced
    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder()
                .requestInterceptor(new UserContextInterceptor());
    }
}
