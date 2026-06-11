package org.jedi_bachelor.bookstatistic.bookservice.repository;

import org.jedi_bachelor.bookstatistic.bookservice.entity.BookAuthorRelation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BookAuthorRepository extends JpaRepository<BookAuthorRelation, UUID> {
    List<BookAuthorRelation> findByAuthorId(UUID authorId);
}
