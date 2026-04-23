package org.jedi_bachelor.bookstatistic.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.bookstatistic.client.AccountClient;
import org.jedi_bachelor.bookstatistic.dto.request.notification.BroadcastMessage;
import org.jedi_bachelor.bookstatistic.emal.EmailContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Data
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private final String emailSenderAddress;

    private final AccountClient accountClient;

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

        mailSender.send(message);
    }

    /**
     * Метод на отправку сообщения на почту всем пользователям, у которых есть флаг
     * true на отправку сообщений по этому каналу
     *
     * @param message сообщение на отправку
     */
    public void sendBroadcastMessage(BroadcastMessage message) {
        List<String> addresses = this.accountClient.getEmailAddresses();

        for(String address : addresses) {
            sendEmail(new EmailContext(
                    address,
                    message.subject(),
                    message.message()
            ));
        }
    }
}
