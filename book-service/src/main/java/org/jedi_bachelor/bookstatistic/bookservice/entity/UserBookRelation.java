package org.jedi_bachelor.bookstatistic.bookservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "users_books")
@Data
@IdClass(UserBookRelation.UserBookRelationId.class)
public class UserBookRelation {
    @Id
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Id
    @Column(name = "book_id", nullable = false)
    private UUID bookId;

    @Column(name = "readed_pages", nullable = false)
    private Integer readedPages;

    @Data
    public static class UserBookRelationId implements Serializable {
        private UUID userId;
        private UUID bookId;
    }
}
