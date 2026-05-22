package org.jedi_bachelor.bookstatistic.bookservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "users_books")
public class UserBookRelation {
    private UUID userId;

    private UUID bookId;

    private Integer readedPages;
}
