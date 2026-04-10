package org.jedi_bachelor.bookstatistic.repository;

import org.jedi_bachelor.bookstatistic.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {
    List<Book> findByUserId();
}
