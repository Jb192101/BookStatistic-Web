package org.jedi_bachelor.bookstatistic.accountservice.outbox.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jedi_bachelor.bookstatistic.accountservice.outbox.OutboxOperation;

import java.util.UUID;

@Entity
@Table(name = "outbox-notification")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OutboxNotificationSettingsMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "email_enable")
    private Boolean emailEnable;

    @Column(name = "email_address")
    private String emailAddress;

    @Column(name = "published")
    private Boolean published;

    @Column(name = "operation")
    @Enumerated(EnumType.STRING)
    private OutboxOperation operation;
}
