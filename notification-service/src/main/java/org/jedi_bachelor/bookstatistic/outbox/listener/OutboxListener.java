package org.jedi_bachelor.bookstatistic.outbox.listener;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.bookstatistic.emal.EmailContext;
import org.jedi_bachelor.bookstatistic.kafka.KafkaProducer;
import org.jedi_bachelor.bookstatistic.outbox.OutboxContextManager;
import org.jedi_bachelor.bookstatistic.outbox.dto.OutboxKafkaDto;
import org.jedi_bachelor.bookstatistic.outbox.entity.OutboxEmailMessage;
import org.jedi_bachelor.bookstatistic.outbox.entity.OutboxKafkaMessage;
import org.jedi_bachelor.bookstatistic.service.EmailService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxListener {
    private final KafkaProducer kafkaProducer;

    private final EmailService emailService;

    private final OutboxContextManager outboxContextManager;

    /**
     * Метод отправки сообщения на почту
     * Предусловие: известно, что пользователь хочет получать рассылку
     */
    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void prePersistOutboxEmailMessage() {
        List<OutboxEmailMessage> messages = this.outboxContextManager.findEmailMessageByStatusFalse();

        for(OutboxEmailMessage message : messages) {
            if(!message.getPublished()) {
                formEmailMessageAndSending(message);

                message.setPublished(true);
                outboxContextManager.saveOutboxEmailMessage(message);
            }
        }
    }

    /**
     * Метод отправки сообщения в Kafka
     */
    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void prePersistOutboxKafkaMessage() {
        List<OutboxKafkaMessage> messages =
                this.outboxContextManager.findKafkaMessageByStatusFalse();

        for(OutboxKafkaMessage message : messages) {
            if(!message.getPublished()) {
                OutboxKafkaDto dto = new OutboxKafkaDto(
                        message.getId(),
                        message.getTitle(),
                        message.getMessageResult()
                );

                this.kafkaProducer.sendMessageToSendingNotificationResultTopic(dto);

                message.setPublished(true);
                this.outboxContextManager.saveOutboxKafkaMessage(message);
            }
        }
    }

    /**
     * Метод формирования сообщения в email и его отправка
     *
     * @param message сущность сообщения
     */
    private void formEmailMessageAndSending(OutboxEmailMessage message) {
        // Формирование EmailContext
        EmailContext emailContext = new EmailContext();
        emailContext.setMessage(message.getMessage());
        emailContext.setSubject(message.getSubject());
        emailContext.setTo(message.getEmailAddress());

        // Отправка сообщения
        this.emailService.sendEmail(emailContext);
    }
}
