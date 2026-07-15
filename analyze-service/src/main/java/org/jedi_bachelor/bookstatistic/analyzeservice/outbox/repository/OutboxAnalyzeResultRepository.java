package org.jedi_bachelor.bookstatistic.analyzeservice.outbox.repository;

import org.jedi_bachelor.bookstatistic.analyzeservice.outbox.entity.OutboxAnalyzeResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxAnalyzeResultRepository extends JpaRepository<OutboxAnalyzeResultEntity, Long> {
}
