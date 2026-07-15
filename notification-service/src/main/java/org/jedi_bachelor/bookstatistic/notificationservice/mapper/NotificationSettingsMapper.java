package org.jedi_bachelor.bookstatistic.notificationservice.mapper;

import org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities.NotificationSettingsDto;
import org.jedi_bachelor.bookstatistic.notificationservice.entity.NotificationSettings;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationSettingsMapper {
    NotificationSettingsDto toDto(NotificationSettings notification);

    List<NotificationSettingsDto> toDtoList(List<NotificationSettings> settings);
}
