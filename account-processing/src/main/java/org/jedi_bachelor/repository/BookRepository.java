package org.jedi_bachelor.repository;

import org.jedi_bachelor.model.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@Repository
@EnableJpaRepositories
public interface BookRepository extends JpaRepository<Book, Long> {
}
