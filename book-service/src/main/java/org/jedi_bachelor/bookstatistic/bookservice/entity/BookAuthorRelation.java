package org.jedi_bachelor.bookstatistic.bookservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "books_authors")
@Data
@IdClass(BookAuthorRelation.BookAuthorRelationId.class)
public class BookAuthorRelation {
    @ManyToOne
    @Column(name = "book_id")
    private Book book;

    @ManyToOne
    @Column(name = "author_id")
    private Author author;

    @Column(name = "author_position")
    private Integer authorPosition;

    @Data
    public static class BookAuthorRelationId implements Serializable {
        private UUID book;
        private UUID author;
    }
}
