package org.jedi_bachelor.bookstatistic.bookservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "book_text")
@IdClass(BookTextRelation.BookTextRelationId.class)
@Data
public class BookTextRelation {
    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne
    @JoinColumn(name = "text_id", nullable = false)
    private Text text;

    @Data
    public static class BookTextRelationId implements Serializable {
        private UUID book;
        private UUID text;
    }
}
