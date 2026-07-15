package org.jedi_bachelor.bookstatistic.analyzeservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "training_data")
public class TrainingData {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID bookId;

    @Column(columnDefinition = "TEXT")
    private String bookText;

    @Column(columnDefinition = "jsonb")
    private String correctGenres;      // {"fantasy": 0.9, "detective": 0.7, ...}

    private LocalDateTime createdAt;

    private boolean usedForTraining;   // использовано ли для обучения
}
