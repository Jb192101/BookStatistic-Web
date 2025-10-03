package org.jedi_bachelor.repository;

import org.jedi_bachelor.model.entities.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {
    Optional<Card> getCardById(Long id);
}
