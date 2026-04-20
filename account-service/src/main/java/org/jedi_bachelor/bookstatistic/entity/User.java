package org.jedi_bachelor.bookstatistic.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "hash_password", nullable = false)
    private String hashPassword;

    @Column(name = "role")
    @Enumerated(value = EnumType.STRING)
    private UserRole role;

    @Column(name = "enable_email", nullable = false, columnDefinition = "default FALSE")
    private Boolean enableEmail;

    @Column(name = "email")
    private String email;

    @Column(name = "telegram_address")
    private String telegramAddress;
}
