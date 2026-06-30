package org.jedi_bachelor.bookstatistic.notificationservice.mapper;

import org.jedi_bachelor.bookstatistic.commonslib.dto.response.notification.InboxNotificationDto;
import org.jedi_bachelor.bookstatistic.notificationservice.inbox.entity.InboxNotificationEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InboxNotificationMapper {
    InboxNotificationDto toDto(InboxNotificationEntity entity);

    List<InboxNotificationDto> toDtoList(List<InboxNotificationEntity> entities);
}
