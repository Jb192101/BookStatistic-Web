package org.jedi_bachelor.bookstatistic.accountservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "keycloak_sub", unique = true, nullable = false)
    private String keycloakSub;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "password_hash", nullable = false)
    private String hashPassword;

    @Column(name = "language", nullable = false, columnDefinition = "DEFAULT 'EN'")
    private String language;

    @Column(name = "created_at", columnDefinition = "DEFAULT DATE()")
    private LocalDateTime createdAt;
}
