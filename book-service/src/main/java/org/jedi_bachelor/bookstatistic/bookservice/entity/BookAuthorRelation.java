package org.jedi_bachelor.bookstatistic.bookservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "books_authors")
@Data
public class BookAuthorRelation {
    @Column(name = "book_id")
    private Book book;

    @Column(name = "author_id")
    private Author author;

    @Column(name = "author_position")
    private Integer authorPosition;
}
