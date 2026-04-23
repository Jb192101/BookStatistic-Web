package org.jedi_bachelor.bookstatistic.outbox.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox-notification")
@Data
public class OutboxNotificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "published")
    private Boolean published;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
