package org.jedi_bachelor.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.Data;
import lombok.NoArgsConstructor;

import org.jedi_bachelor.model.enums.ReadingStatus;

// Нужно добавить валидацию на то, что readedPages меньше или равен totalPages для изменения состояния

@Entity
@Table(name="reading_states")
@Data
@NoArgsConstructor
public class ReadingState {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="account_id")
    @NotNull
    private Long accountId;

    @Column(name="book_id")
    @NotNull
    private Long bookId;

    @Column(name="readed_pages")
    @Positive
    private Integer readedPages;

    @Column(name="reading_status")
    @Enumerated(EnumType.STRING)
    private ReadingStatus readingStatus;
}
