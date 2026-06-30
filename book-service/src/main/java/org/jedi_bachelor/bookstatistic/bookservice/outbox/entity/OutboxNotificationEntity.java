package org.jedi_bachelor.bookstatistic.bookservice.outbox.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jedi_bachelor.ioboxstarter.core.OutboxMessage;

@Entity
@Table(name = "outbox_notifications")
@Data
@EqualsAndHashCode(callSuper = true)
public class OutboxNotificationEntity extends OutboxMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
