package org.jedi_bachelor.bookstatistic.outbox.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.jedi_bachelor.bookstatistic.outbox.interfaces.OutboxEnable;
import org.jedi_bachelor.bookstatistic.outbox.listener.OutboxListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(OutboxListener.class)
@Table(name = "outbox-email")
@Data
public class OutboxEmailMessage implements OutboxEnable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subject")
    private String subject;

    @Column(name = "message")
    private String message;

    @Column(name = "email_address")
    private String emailAddress;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "broadcasted")
    private Boolean broadcasted; // распространяется ли на все адреса

    @Column(name = "published")
    private Boolean published;
}
