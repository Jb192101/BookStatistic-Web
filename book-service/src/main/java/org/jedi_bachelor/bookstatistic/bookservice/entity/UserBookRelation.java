package org.jedi_bachelor.bookstatistic.bookservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "users_books")
@Data
@IdClass(UserBookRelation.UserBookRelationId.class)
public class UserBookRelation {
    @Column(name = "user_id", nullable = false)
    private UUID userId;

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
