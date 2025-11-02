package org.jedi_bachelor.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jedi_bachelor.model.enums.GenreType;

@Entity
@Data
@Table(name="books")
@NoArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="title")
    private String title;

    @Column(name="author")
    private String author;

    @Column(name="genre")
    @Enumerated(value = EnumType.STRING)
    private GenreType genreType;

    @Column(name="total_pages")
    @Positive
    private Integer totalPages;

    @Column(name="total_rating")
    @DecimalMin(value="1.0")
    @DecimalMax(value="5.0")
    private Float totalRating;
}
