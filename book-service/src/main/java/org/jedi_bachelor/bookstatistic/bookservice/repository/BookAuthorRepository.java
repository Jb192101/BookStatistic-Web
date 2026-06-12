package org.jedi_bachelor.bookstatistic.bookservice.repository;

import org.jedi_bachelor.bookstatistic.bookservice.entity.BookAuthorRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookAuthorRepository extends JpaRepository<BookAuthorRelation, BookAuthorRelation.BookAuthorRelationId> {
    @Query("SELECT bar FROM BookAuthorRelation bar WHERE bar.author.id = :authorId")
    List<BookAuthorRelation> findByAuthorId(@Param("authorId") UUID authorId);

    Optional<BookAuthorRelation> findByBook_IdAndAuthor_Id(UUID bookId, UUID authorId);

    boolean existsByBook_IdAndAuthor_Id(UUID bookId, UUID authorId);

    void deleteByBook_IdAndAuthor_Id(UUID bookId, UUID authorId);
}
