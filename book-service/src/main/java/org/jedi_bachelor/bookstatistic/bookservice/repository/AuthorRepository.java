package org.jedi_bachelor.bookstatistic.bookservice.repository;

import org.jedi_bachelor.bookstatistic.bookservice.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AuthorRepository extends JpaRepository<Author, UUID> {
    /**
     * Найти автора по имени, фамилии и отчеству
     *
     * @param firstName  имя
     * @param middleName отчество (может быть null)
     * @param lastName   фамилия
     * @return Optional с автором, если найден
     */
    Optional<Author> findByFirstNameAndMiddleNameAndLastName(
            String firstName,
            String middleName,
            String lastName
    );
}
