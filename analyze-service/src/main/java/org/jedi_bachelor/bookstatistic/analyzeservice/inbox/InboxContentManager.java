package org.jedi_bachelor.bookstatistic.analyzeservice.inbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.analyzeservice.inbox.entity.InboxAnalyzeEntity;
import org.jedi_bachelor.bookstatistic.analyzeservice.inbox.repository.InboxAnalyzeRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class InboxContentManager {
    private final InboxAnalyzeRepository inboxAnalyzeRepository;

    public InboxAnalyzeEntity save(InboxAnalyzeEntity entity) {
        return this.inboxAnalyzeRepository.save(entity);
    }

    public List<InboxAnalyzeEntity> findEntitiesWithStatus(boolean status) {
        return this.inboxAnalyzeRepository.findAll().stream().filter(e -> e.getProcessed() == status).toList();
    }
}
