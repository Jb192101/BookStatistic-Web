package org.jedi_bachelor.bookstatistic.analyzeservice.inbox.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.analyzeservice.inbox.InboxContentManager;
import org.jedi_bachelor.bookstatistic.analyzeservice.inbox.entity.InboxAnalyzeEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class InboxProcessorService {
    private final InboxContentManager inboxContentManager;

    @Transactional
    public void processOneInboxMessage(InboxAnalyzeEntity entity) {

    }
}
