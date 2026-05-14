package org.jedi_bachelor.bookstatistic.notificationservice.converter;


import org.jedi_bachelor.bookstatistic.commonslib.converter.Converter;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.notification.NotificationCreationDto;
import org.jedi_bachelor.bookstatistic.notificationservice.entity.Notification;

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
