package org.jedi_bachelor.model.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name="achievements_account_relations")
@Data
public class AchievementAccountRelation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name="achievement_id")
    private Long achievementId;

    @Column(name="date")
    private LocalDateTime dateOfGetting;
}
