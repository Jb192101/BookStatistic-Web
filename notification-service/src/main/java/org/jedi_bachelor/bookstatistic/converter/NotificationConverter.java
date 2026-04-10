package org.jedi_bachelor.bookstatistic.converter;

import org.jedi_bachelor.bookstatistic.dto.request.notification.NotificationCreationDto;
import org.jedi_bachelor.bookstatistic.entity.Notification;

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
        return null;
    }
}
