package org.jedi_bachelor.bookstatistic.outbox.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "outbox-book")
@Data
public class OutboxBookMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "action")
    private String action; // delete, add

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "published")
    private Boolean published;
}