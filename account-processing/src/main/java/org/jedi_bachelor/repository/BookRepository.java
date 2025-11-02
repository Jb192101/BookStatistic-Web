package org.jedi_bachelor.repository;

import org.jedi_bachelor.model.entities.Book;
import org.jedi_bachelor.model.enums.GenreType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findBookByGenreType(GenreType genreType);
}
