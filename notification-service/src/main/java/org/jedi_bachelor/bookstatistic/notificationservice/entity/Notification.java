package org.jedi_bachelor.bookstatistic.notificationservice.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import org.jedi_bachelor.bookstatistic.notificationservice.entity.enums.NotificationType;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@Entity
@Table(name = "notifications")
@Data
@Builder
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
