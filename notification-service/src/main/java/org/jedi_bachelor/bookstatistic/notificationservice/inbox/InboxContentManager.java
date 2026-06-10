package org.jedi_bachelor.bookstatistic.notificationservice.inbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.commonslib.converter.Converter;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.notification.NotificationCreationDto;
import org.jedi_bachelor.bookstatistic.notificationservice.inbox.entity.InboxNotificationEntity;
import org.jedi_bachelor.bookstatistic.notificationservice.inbox.repository.InboxNotificationRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class InboxContentManager {
    private final Converter<InboxNotificationEntity, NotificationCreationDto> converter;

    private final InboxNotificationRepository inboxNotificationRepository;

    /**
     * Метод сохранения сущности inbox через DTO
     *
     * @param dto DTO создания уведомления
     * @return сохранённая сущность
     */
    public InboxNotificationEntity save(NotificationCreationDto dto) {
        InboxNotificationEntity entity = this.converter.convert(dto);

        return this.save(entity);
    }

    /**
     * Метод поиска сущностей с определённым статусом обработки
     *
     * @param processedFlag флаг обработки
     * @return список сущностей
     */
    public List<InboxNotificationEntity> findEntitiesWithStatus(boolean processedFlag) {
        return this.inboxNotificationRepository.findAll()
                .stream()
                .filter(e -> e.getProcessed() == processedFlag)
                .toList();
    }

    public InboxNotificationEntity save(InboxNotificationEntity entity) {
        return this.inboxNotificationRepository.save(entity);
    }
}
