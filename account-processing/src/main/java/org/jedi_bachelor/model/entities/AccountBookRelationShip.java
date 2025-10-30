package org.jedi_bachelor.model.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.jedi_bachelor.model.enums.Rating;

import java.time.LocalDateTime;

@Entity
@Table(name="account_book")
@Data
public class AccountBookRelationShip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name="readed_pages")
    private Integer readedPages;

    @Column(name = "started_date")
    private LocalDateTime startedDate;

    @Column(name = "is_finished")
    private Boolean isFinished;

    @Column(name = "rating")
    @Enumerated(EnumType.STRING)
    private Rating rating;
}
