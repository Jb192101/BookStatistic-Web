package org.jedi_bachelor.bookstatistic.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "book_text")
@Data
public class BookTextRelation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
}
