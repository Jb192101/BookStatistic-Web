package org.jedi_bachelor.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.jedi_bachelor.model.enums.Rating;

@Entity
@Data
@Table(name="books")
public class Book {
    @Id
    @ManyToMany
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="title")
    private String title;

    @Column(name="author")
    private String author;

    @Column(name="total_pages")
    @Positive
    private Integer totalPages;

    @Column(name="rating")
    @Enumerated(EnumType.STRING)
    private Rating rating;
}
