package org.jedi_bachelor.bookstatistic.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "notification_settings")
public class NotificationSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", unique = true)
    private UUID userId;

    @Column(name = "email_enable")
    private Boolean emailEnable;

    @Column(name = "email_address")
    private String emailAddress;

    public NotificationSettings(UUID userId) {
        this.userId = userId;
    }

    @PrePersist
    private void prePersist() {
        if(this.id == null) {
            this.id = UUID.randomUUID();
        }

        if(this.emailEnable == null) {
            this.emailEnable = false;
        }

        if(this.emailAddress == null) {
            this.emailAddress = "";
        }
    }
}
