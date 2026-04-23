package org.jedi_bachelor.bookstatistic.outbox.listener;

import jakarta.persistence.PrePersist;
import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.bookstatistic.dto.kafka.KafkaMessageDto;
import org.jedi_bachelor.bookstatistic.emal.EmailContext;
import org.jedi_bachelor.bookstatistic.kafka.KafkaProducer;
import org.jedi_bachelor.bookstatistic.outbox.entity.OutboxEmailMessage;
import org.jedi_bachelor.bookstatistic.outbox.entity.OutboxKafkaMessage;
import org.jedi_bachelor.bookstatistic.service.EmailService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxListener {
    private final KafkaProducer kafkaProducer;

    private final EmailService emailService;

    /**
     * Метод отправки сообщения на почту
     * Предусловие: известно, что пользователь хочет получать рассылку
     *
     * @param message сущность сообщения
     */
    @PrePersist
    public void prePersistOutboxEmailMessage(OutboxEmailMessage message) {
        // Формирование EmailContext
        EmailContext emailContext = new EmailContext();
        emailContext.setMessage(message.getMessage());
        emailContext.setSubject(message.getSubject());
        emailContext.setTo(message.getEmailAddress());

        // Отправка сообщения
        this.emailService.sendEmail(emailContext);
    }

    /**
     * Метод отправки сообщения в Kafka
     *
     * @param message сущность сообщения
     */
    @PrePersist
    public void prePersistOutboxKafkaMessage(OutboxKafkaMessage message) {
        // Формирование KafkaMessageDto
        KafkaMessageDto dto = new KafkaMessageDto();

        // Отправка в Kafka
    }
}
