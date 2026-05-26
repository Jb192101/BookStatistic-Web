package org.jedi_bachelor.bookstatistic.accountservice.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
@ConfigurationProperties(prefix = "example")
@Getter
@Setter
public class ServiceConfiguration {
    @Value("${rest.template.connect-timeout:5000}")
    private int connectTimeout;

    @Value("${rest.template.read-timeout:10000}")
    private int readTimeout;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofMillis(this.connectTimeout))
                .setReadTimeout(Duration.ofMillis(this.readTimeout))
                .build();
    }
}
