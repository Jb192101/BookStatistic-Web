package org.jedi_bachelor.bookstatistic.bookservice.outbox.repository;

import org.jedi_bachelor.bookstatistic.bookservice.outbox.entity.OutboxAnalyzeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxAnalyzeRepository extends JpaRepository<OutboxAnalyzeEntity, Long> {
}
