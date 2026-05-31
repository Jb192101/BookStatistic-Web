package org.jedi_bachelor.bookstatistic.analyzeservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "user_book_reports")
public class UserBookReport {
    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "following_genres", columnDefinition = "jsonb")
    private String followingGenres;
}


