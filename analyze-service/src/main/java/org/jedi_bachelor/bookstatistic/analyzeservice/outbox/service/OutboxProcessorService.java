package org.jedi_bachelor.bookstatistic.analyzeservice.outbox.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.analyzeservice.outbox.entity.OutboxAnalyzeResultEntity;
import org.jedi_bachelor.bookstatistic.analyzeservice.outbox.repository.OutboxAnalyzeResultRepository;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OutboxProcessorService {
    private final OutboxAnalyzeResultRepository outboxAnalyzeResultRepository;

    @Transactional
    public void processOneOutboxMessage(OutboxAnalyzeResultEntity entity) {

    }
}
