package org.jedi_bachelor.bookstatistic.outbox.entity;

import jakarta.persistence.*;
import lombok.Data;

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
}
