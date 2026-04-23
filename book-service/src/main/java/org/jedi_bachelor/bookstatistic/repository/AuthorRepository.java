package org.jedi_bachelor.bookstatistic.repository;

import org.jedi_bachelor.bookstatistic.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuthorRepository extends JpaRepository<Author, UUID> {
}
