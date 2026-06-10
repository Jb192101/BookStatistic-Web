package org.jedi_bachelor.bookstatistic.notificationservice.inbox.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.jedi_bachelor.bookstatistic.notificationservice.inbox.InboxOperation;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "inbox_notifications")
@Data
public class InboxNotificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private InboxOperation type;

    @Column(name = "notification_title", nullable = false)
    private String notificationTitle;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "processed", nullable = false)
    private Boolean processed = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "retry_count")
    private int retryCount = 0;

    /**
     * Инкремент кол-ва повторных попыток
     *
     * @return нынешнее кол-во попыток
     */
    public int incrementRetryCount() {
        return this.retryCount++;
    }
}
