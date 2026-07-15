package org.jedi_bachelor.bookstatistic.notificationservice.inbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.notification.NotificationCreationDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.response.notification.InboxNotificationDto;
import org.jedi_bachelor.bookstatistic.notificationservice.converter.NotificationDtoConverter;
import org.jedi_bachelor.bookstatistic.notificationservice.inbox.entity.InboxNotificationEntity;
import org.jedi_bachelor.bookstatistic.notificationservice.inbox.repository.InboxNotificationRepository;
import org.jedi_bachelor.bookstatistic.notificationservice.mapper.InboxNotificationMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class InboxContentManager {
    private final NotificationDtoConverter converter;

    private final InboxNotificationRepository inboxNotificationRepository;

    private final InboxNotificationMapper inboxNotificationMapper;

    public List<InboxNotificationDto> findAllInboxNotifications() {
        List<InboxNotificationEntity> entities = this.inboxNotificationRepository.findAll();

        return this.inboxNotificationMapper.toDtoList(entities);
    }

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
