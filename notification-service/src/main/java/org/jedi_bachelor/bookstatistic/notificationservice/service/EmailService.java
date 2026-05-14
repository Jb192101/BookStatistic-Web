package org.jedi_bachelor.bookstatistic.notificationservice.service;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.notification.BroadcastMessage;
import org.jedi_bachelor.bookstatistic.commonslib.internalinteraction.InteractionClient;
import org.jedi_bachelor.bookstatistic.notificationservice.emal.EmailContext;
import org.jedi_bachelor.bookstatistic.notificationservice.repository.NotificationSettingsRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Data
@RequiredArgsConstructor
@Slf4j
@Builder
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private final String emailSenderAddress;

    @Qualifier("accountInteractionClient")
    private final InteractionClient accountClient;

    private final NotificationSettingsRepository notificationSettingsRepository;

    /**
     * Метод отправки email (единичный)
     *
     * @param context контекст отправки
     */
    public void sendEmail(EmailContext context) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(context.getTo());
        message.setSubject(context.getSubject());
        message.setText(context.getMessage());
        message.setFrom(this.emailSenderAddress);

        log.info("Message succesfully created, message: {}", message);

        this.mailSender.send(message);

        log.info("Message {} succesfully sended", message);
    }

    /**
     * Метод на отправку сообщения на почту всем пользователям, у которых есть флаг
     * true на отправку broadcast сообщений
     *
     * @param message сообщение на отправку
     */
    public void sendBroadcastMessage(BroadcastMessage message) {
        List<String> addresses = (List<String>) this.notificationSettingsRepository.findByEnableGettingBroadcastMessages(true);

        log.info("Email addresses has got {}", addresses);

        this.sendBroadcastMessageWithAddresses(message, addresses);
    }

    /**
     * Метод отправки на email-адреса
     * Предусловие: все адреса предварительно получены
     *
     * @param message сообщение для отправки
     * @param addresses адреса, на которые надо рассылать broadcast-сообщение
     */
    private void sendBroadcastMessageWithAddresses(BroadcastMessage message, List<String> addresses) {
        for(String address : addresses) {
            this.sendEmail(new EmailContext(
                    address,
                    message.subject(),
                    message.message()
            ));
        }
    }
}
