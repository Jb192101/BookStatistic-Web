package org.jedi_bachelor.model.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="account-book")
@Data
public class AccountBookRelationShip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long bookId;
    private Integer readedPages;
}
