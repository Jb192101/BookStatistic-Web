package org.jedi_bachelor.bookstatistic.notificationservice.configuration;

import org.jedi_bachelor.bookstatistic.notificationservice.repository.NotificationSettingsRepository;
import org.jedi_bachelor.bookstatistic.notificationservice.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfiguration {
    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Bean
    @Primary
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(this.host);
        mailSender.setPort(this.port);
        mailSender.setUsername(this.username);
        mailSender.setPassword(this.password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.starttls.enable", "false");
        props.put("mail.debug", "true");

        return mailSender;
    }

    @Bean
    @Order(3)
    public EmailService emailService(JavaMailSender javaMailSender,
                                     NotificationSettingsRepository notificationSettingsRepository) {
        return EmailService.builder()
                .mailSender(javaMailSender)
                .notificationSettingsRepository(notificationSettingsRepository)
                .build();
    }
}
