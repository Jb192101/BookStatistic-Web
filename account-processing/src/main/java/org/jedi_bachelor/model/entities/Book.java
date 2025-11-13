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

    @Column(name="stars_5_count")
    private Integer fiveStartCount;

    @Column(name="stars_4_count")
    private Integer fourStarsCount;

    @Column(name="stars_3_count")
    private Integer threeStarsCount;

    @Column(name="stars_2_count")
    private Integer twoStartsCount;

    @Column(name="stars_5_count")
    private Integer oneStartCount;
}
