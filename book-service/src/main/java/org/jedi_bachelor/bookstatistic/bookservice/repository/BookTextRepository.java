package org.jedi_bachelor.bookstatistic.bookservice.repository;

import org.jedi_bachelor.bookstatistic.bookservice.entity.BookTextRelation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookTextRepository extends JpaRepository<BookTextRelation, UUID> {
}
