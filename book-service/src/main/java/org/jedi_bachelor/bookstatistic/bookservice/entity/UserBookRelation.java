package org.jedi_bachelor.bookstatistic.bookservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users_books")
@Data
@IdClass(UserBookRelation.UserBookRelationId.class)
public class UserBookRelation {
    @EmbeddedId
    private UserBookRelationId id;

    @Column(name = "readed_pages", nullable = false)
    private Integer readedPages;

    @Column(name = "last_opening_book_time")
    private LocalDateTime lastOpeningBookTime;

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
    public static class UserBookRelationId implements Serializable {
        @Column(name = "user_id", nullable = false)
        private UUID userId;

        @Column(name = "book_id", nullable = false)
        private UUID bookId;
    }
}
