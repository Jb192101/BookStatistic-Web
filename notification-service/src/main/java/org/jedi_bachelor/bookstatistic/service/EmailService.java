package org.jedi_bachelor.bookstatistic.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.bookstatistic.emal.AbstractEmailContext;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Data
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    private final String emailSender;

    /**
     * Метод отправки email
     *
     * @param context
     */
    public void sendEmail(AbstractEmailContext context) {
        SimpleMailMessage message = new SimpleMailMessage();
        //message.setTo(to);
        //message.setSubject(subject);
        //message.setText(text);
        message.setFrom(this.emailSender);

        mailSender.send(message);
    }
}
