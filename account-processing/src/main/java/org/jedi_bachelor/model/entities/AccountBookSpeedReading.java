package org.jedi_bachelor.model.entities;

/*
Класс для отображения скорости чтения по месяцам / статистики всего прочитанных страниц
 */

import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name="account-book-speed")
@Data
public class AccountBookSpeedReading {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "speed_reading_pages")
    @PositiveOrZero
    private Integer speedReadingPages;
}