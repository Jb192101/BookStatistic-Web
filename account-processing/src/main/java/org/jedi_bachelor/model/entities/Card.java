package org.jedi_bachelor.model.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="cards")
@Data
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="card_number")
    private String cardNumber;

    // Поле-заглушка
    @Column(name="balance")
    private Integer balance;
}
