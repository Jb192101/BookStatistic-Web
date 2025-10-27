package org.jedi_bachelor.configuration;

import org.jedi_bachelor.email.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("application.properties")
public class EmailConfig {
    @Value("spring.mail.username")
    private String emailSender;

    @Bean
    public EmailService emailService() {
        return new EmailService(emailSender);
    }
}
