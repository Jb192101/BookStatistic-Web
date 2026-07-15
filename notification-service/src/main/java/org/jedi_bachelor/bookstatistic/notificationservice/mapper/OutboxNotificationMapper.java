package org.jedi_bachelor.bookstatistic.notificationservice.mapper;

import org.jedi_bachelor.bookstatistic.commonslib.dto.response.notification.OutboxNotificationDto;
import org.jedi_bachelor.bookstatistic.notificationservice.outbox.entity.OutboxKafkaMessage;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OutboxNotificationMapper {
    OutboxNotificationDto toDto(OutboxKafkaMessage entity);

    List<OutboxNotificationDto> toDtoList(List<OutboxKafkaMessage> entities);
}
