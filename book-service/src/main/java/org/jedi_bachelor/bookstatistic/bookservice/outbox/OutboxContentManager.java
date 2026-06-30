package org.jedi_bachelor.bookstatistic.bookservice.outbox;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.bookservice.mapper.OutboxAnalyzeEntityMapper;
import org.jedi_bachelor.bookstatistic.bookservice.outbox.entity.OutboxAnalyzeEntity;
import org.jedi_bachelor.bookstatistic.bookservice.outbox.repository.OutboxAnalyzeRepository;
import org.jedi_bachelor.bookstatistic.bookservice.outbox.repository.OutboxNotificationEntityRepository;
import org.jedi_bachelor.bookstatistic.commonslib.dto.response.book.OutboxAnalyzeDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class OutboxContentManager {
    private final OutboxAnalyzeRepository outboxAnalyzeRepository;

    private final OutboxAnalyzeEntityMapper outboxAnalyzeEntityMapper;

    private final OutboxNotificationEntityRepository outboxNotificationEntityRepository;

    @Transactional
    public List<OutboxAnalyzeEntity> findAnalyzeMessageByStatusFalse() {
        return this.outboxAnalyzeRepository.findAll();//.stream().filter(e -> !e.getPublished()).toList();
    }

    public OutboxAnalyzeEntity save(OutboxAnalyzeEntity entity) {
        return this.outboxAnalyzeRepository.save(entity);
    }

    public List<OutboxAnalyzeDto> findAllOutboxAnalyzeMessages() {
        List<OutboxAnalyzeEntity> result = this.outboxAnalyzeRepository.findAll();

        return this.outboxAnalyzeEntityMapper.toDtoList(result);
    }
}
