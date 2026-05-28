package org.jedi_bachelor.bookstatistic.analyzeservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "book_analysis")
public class BookAnalysis {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID analysisId;

    @Column(name = "book_id", nullable = false)
    private UUID bookId;

    @Column(nullable = false)
    private LocalDateTime analyzedAt;

    @Column(nullable = false)
    private String modelVersion;

    @Column(columnDefinition = "jsonb")
    private String genres;

    @Column(columnDefinition = "jsonb")
    private String subgenres;

    @Column(columnDefinition = "jsonb")
    private String narrativeElements;

    @Column(columnDefinition = "jsonb")
    private String pacing;

    @Column(columnDefinition = "jsonb")
    private String atmosphere;

    @Column(columnDefinition = "jsonb")
    private String tokens;

    @Column(nullable = false)
    private Float confidenceScore;
}
