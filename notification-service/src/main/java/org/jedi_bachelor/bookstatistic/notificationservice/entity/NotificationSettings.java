package org.jedi_bachelor.bookstatistic.notificationservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "notification_settings")
@Data
@NoArgsConstructor
public class NotificationSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", unique = true)
    private UUID userId;

    @Column(name = "enable_email", nullable = false)
    private Boolean enableEmail = false;

    @Column(name = "enable_broadcasting", nullable = false)
    private Boolean enableGettingBroadcastMessages = false;

    @Column(name = "email")
    @Email
    private String email;

    @Column(name = "telegram_address")
    private String telegramAddress;

    @Column(name = "enable_telegram", nullable = false)
    private Boolean enableTelegram = false;

    public NotificationSettings(UUID userId) {
        this.userId = userId;
    }

    @PrePersist
    private void prePersist() {
        if(this.id == null) {
            this.id = UUID.randomUUID();
        }

        if(this.enableEmail == null) {
            this.enableEmail = false;
        }

        if(this.email == null) {
            this.email = "";
        }

        if(this.enableTelegram == null) {
            this.enableTelegram = false;
        }

        if(this.telegramAddress == null) {
            this.telegramAddress = "";
        }

        if(this.enableGettingBroadcastMessages == null) {
            this.enableGettingBroadcastMessages = false;
        }
    }
}
