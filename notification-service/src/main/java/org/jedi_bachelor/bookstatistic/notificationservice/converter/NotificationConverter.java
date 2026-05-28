package org.jedi_bachelor.bookstatistic.notificationservice.converter;

import org.jedi_bachelor.bookstatistic.commonslib.converter.Converter;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.notification.NotificationCreationDto;
import org.jedi_bachelor.bookstatistic.notificationservice.entity.Notification;
import org.jedi_bachelor.bookstatistic.notificationservice.entity.enums.NotificationType;
import org.springframework.stereotype.Component;

@Component
public class NotificationConverter
        implements Converter<Notification, NotificationCreationDto> {
    /**
     * Конвертация NotificationCreationDto в Notification
     *
     * @param dto DTO создания
     * @return сущность Notification
     */
    @Override
    public Notification convert(NotificationCreationDto dto) {
        return Notification.builder()
                .notificationTitle(dto.notificationTitle())
                .message(dto.message())
                .type(NotificationType.valueOf(dto.type()))
                .userId(dto.userId())
                .build();
    }
}
