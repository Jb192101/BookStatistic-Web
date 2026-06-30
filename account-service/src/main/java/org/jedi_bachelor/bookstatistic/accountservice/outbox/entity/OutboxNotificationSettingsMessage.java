package org.jedi_bachelor.bookstatistic.accountservice.outbox.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.jedi_bachelor.ioboxstarter.core.OutboxMessage;
import org.springframework.http.HttpMethod;

import java.util.UUID;

@Entity
@Table(name = "outbox_notification")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OutboxNotificationSettingsMessage extends OutboxMessage {
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "email_enable")
    private Boolean emailEnable;

    @Column(name = "email_address")
    private String emailAddress;

    @Column(name = "telegram")
    private String telegram;

    @Column(name = "broadcast_enable")
    private Boolean enableBroadcast;

    @Column(name = "operation")
    @Enumerated(EnumType.STRING)
    private OutboxOperation operation;
}
