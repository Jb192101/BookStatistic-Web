package org.jedi_bachelor.bookstatistic.notificationservice.outbox.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.jedi_bachelor.bookstatistic.notificationservice.outbox.interfaces.OutboxEnable;
import org.jedi_bachelor.bookstatistic.notificationservice.outbox.listener.OutboxListener;

import java.util.UUID;

@Entity
@EntityListeners(OutboxListener.class)
@Table(name = "outbox_kafka")
@Data
public class OutboxKafkaMessage implements OutboxEnable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "notification_id")
    private UUID notiicationId;

    @Column(name = "title")
    private String title;

    @Column(name = "message_result")
    private String messageResult;

    @Column(name = "published")
    private Boolean published;
}
