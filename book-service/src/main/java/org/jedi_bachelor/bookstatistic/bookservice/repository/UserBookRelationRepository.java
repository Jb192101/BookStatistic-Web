package org.jedi_bachelor.bookstatistic.bookservice.repository;

import org.jedi_bachelor.bookstatistic.bookservice.entity.UserBookRelation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserBookRelationRepository
        extends JpaRepository<UserBookRelation, UserBookRelation.UserBookRelationId> {

    List<UserBookRelation> findById_UserId(UUID userId);
}