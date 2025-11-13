package org.jedi_bachelor.email;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Data
@RequiredArgsConstructor
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    private final String emailSender;

    /*
    * Метод отправки email-сообщения
    * @param to (String) - почта, на которую надо отправить сообщение
    * @param subject (String) - заголовок
    * @param text (String) - текст сообщения
     */
    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom(this.emailSender);

        mailSender.send(message);
    }
}
