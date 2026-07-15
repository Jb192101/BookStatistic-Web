package org.jedi_bachelor.bookstatistic.analyzeservice.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.analyzeservice.outbox.entity.OutboxAnalyzeResultEntity;
import org.jedi_bachelor.bookstatistic.analyzeservice.outbox.repository.OutboxAnalyzeResultRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class OutboxContentManager {
    private final OutboxAnalyzeResultRepository outboxAnalyzeResultRepository;

    public List<OutboxAnalyzeResultEntity> findEntitiesWithStatus(boolean status) {
        return this.outboxAnalyzeResultRepository.findAll().stream().filter(e -> e.isPublished() == status).toList();
    }
}
