package org.jedi_bachelor.bookstatistic.outbox.repository;

import org.jedi_bachelor.bookstatistic.outbox.entity.OutboxAnalyzeMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxAnalyzeMessageRepository
        extends JpaRepository<OutboxAnalyzeMessage, Long> {
}
