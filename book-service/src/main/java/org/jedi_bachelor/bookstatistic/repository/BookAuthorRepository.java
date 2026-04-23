package org.jedi_bachelor.bookstatistic.repository;

import org.jedi_bachelor.bookstatistic.entity.BookAuthorRelation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookAuthorRepository extends JpaRepository<BookAuthorRelation, UUID> {
}
