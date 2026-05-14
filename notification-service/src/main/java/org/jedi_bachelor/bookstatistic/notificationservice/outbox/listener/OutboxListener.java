package org.jedi_bachelor.bookstatistic.notificationservice.outbox.listener;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.notificationservice.emal.EmailContext;
import org.jedi_bachelor.bookstatistic.notificationservice.kafka.KafkaProducer;
import org.jedi_bachelor.bookstatistic.notificationservice.outbox.OutboxContextManager;
import org.jedi_bachelor.bookstatistic.notificationservice.outbox.dto.OutboxKafkaDto;
import org.jedi_bachelor.bookstatistic.notificationservice.outbox.entity.OutboxEmailMessage;
import org.jedi_bachelor.bookstatistic.notificationservice.outbox.entity.OutboxKafkaMessage;
import org.jedi_bachelor.bookstatistic.notificationservice.service.EmailService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
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
                this.formEmailMessageAndSending(message);

                message.setPublished(true);
                this.outboxContextManager.saveOutboxEmailMessage(message);

                log.info("Message {} has published", message);
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

                log.info("Message {} has published", message);
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
