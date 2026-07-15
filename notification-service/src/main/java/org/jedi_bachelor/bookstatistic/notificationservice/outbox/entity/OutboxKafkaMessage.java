package org.jedi_bachelor.bookstatistic.notificationservice.outbox.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.jedi_bachelor.ioboxstarter.core.OutboxMessage;

import java.util.UUID;

@Entity
@Table(name = "outbox_kafka")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OutboxKafkaMessage extends OutboxMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "notification_id")
    private UUID notificationId;

    @Column(name = "title")
    private String title;

    @Column(name = "message_result")
    private String messageResult;
}
