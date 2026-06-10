package org.jedi_bachelor.bookstatistic.notificationservice.converter;

import org.jedi_bachelor.bookstatistic.commonslib.converter.Converter;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.notification.NotificationCreationDto;
import org.jedi_bachelor.bookstatistic.notificationservice.inbox.InboxOperation;
import org.jedi_bachelor.bookstatistic.notificationservice.inbox.entity.InboxNotificationEntity;

public class NotificationDtoConverter implements Converter<InboxNotificationEntity, NotificationCreationDto> {
    @Override
    public InboxNotificationEntity convert(NotificationCreationDto dto) {
        InboxNotificationEntity entity = new InboxNotificationEntity();
        entity.setType(InboxOperation.valueOf(dto.type()));
        entity.setMessage(dto.message());
        entity.setNotificationTitle(dto.notificationTitle());
        entity.setUserId(dto.userId());

        return entity;
    }
}
