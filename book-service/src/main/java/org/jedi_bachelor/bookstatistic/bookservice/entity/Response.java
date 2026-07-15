package org.jedi_bachelor.bookstatistic.bookservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "responses")
@Data
@NoArgsConstructor
public class Response {
    @EmbeddedId
    private ResponseId id;

    @Column(name = "stars")
    @Enumerated(EnumType.STRING)
    private Stars stars;

    @Column(name = "response_text", columnDefinition = "text")
    private String responseText;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Transient
    public UUID getUserId() {
        return this.id.getUserId();
    }

    @Transient
    public UUID getBookId() {
        return this.id.getBookId();
    }

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseId {
        @Column(name = "user_id")
        private UUID userId;

        @Column(name = "book_id")
        private UUID bookId;
    }
}
