package org.jedi_bachelor.bookstatistic.bookservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "books_authors")
@Data
@IdClass(BookAuthorRelation.BookAuthorRelationId.class)
public class BookAuthorRelation {
    @Id
    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Id
    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;

    @Column(name = "author_position")
    private Integer authorPosition;

    @Data
    @EqualsAndHashCode
    public static class BookAuthorRelationId implements Serializable {
        private UUID book;
        private UUID author;
    }
}
