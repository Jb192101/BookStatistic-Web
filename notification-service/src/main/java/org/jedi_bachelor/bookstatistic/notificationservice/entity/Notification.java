package org.jedi_bachelor.bookstatistic.notificationservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jedi_bachelor.bookstatistic.notificationservice.entity.enums.NotificationType;

import java.util.UUID;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "type")
    @Enumerated(value = EnumType.STRING)
    private NotificationType type;

    @Column(name = "notification_title")
    private String notificationTitle;

    @Column(name = "message")
    private String message;
}
