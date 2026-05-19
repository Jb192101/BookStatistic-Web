package org.jedi_bachelor.bookstatistic.accountservice.outbox.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.jedi_bachelor.bookstatistic.accountservice.outbox.OutboxOperation;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "outbox-analyze")
@Data
public class OutboxAnalyzeMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "action")
    @Enumerated(EnumType.STRING)
    private OutboxOperation action; // delete, add

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "published")
    private Boolean published;
}
