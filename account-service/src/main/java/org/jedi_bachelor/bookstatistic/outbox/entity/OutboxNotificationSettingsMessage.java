package org.jedi_bachelor.bookstatistic.outbox.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
