package org.jedi_bachelor.bookstatistic.service;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.bookstatistic.converter.NotificationConverter;
import org.jedi_bachelor.bookstatistic.dto.request.notification.NotificationCreationDto;
import org.jedi_bachelor.bookstatistic.dto.mapentities.NotificationDto;
import org.jedi_bachelor.bookstatistic.emal.AbstractEmailContext;
import org.jedi_bachelor.bookstatistic.entity.Notification;
import org.jedi_bachelor.bookstatistic.mapper.NotificationMapper;
import org.jedi_bachelor.bookstatistic.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final EmailService emailService;

    private final NotificationRepository notificationRepository;

    private final NotificationMapper notificationMapper;

    private final NotificationConverter converter;

    public void addNewNotification(NotificationCreationDto dto) {
        // Создание нового уведомления
        Notification notification = this.converter.convertDtoToEntity(dto);

        // Сохранение

        // Получение настроек у пользователя

        // Если настройки удовлетворены, отправляем сообщение в email
    }

    public List<NotificationDto> getAllNotifications() {
        return this.notificationMapper.toDtoList(
                this.notificationRepository.findAll()
        );
    }

    public List<NotificationDto> getUserNotifications(UUID userId) {
        return this.notificationMapper.toDtoList(
                this.notificationRepository.findByUserId(userId)
        );
    }

    public NotificationDto getNotification(UUID notificationId) {
        return this.notificationMapper.toDto(
                this.notificationRepository.findByNotificationId(notificationId)
        );
    }

    private void sendEmailMessage() {
        // Заглушка
        this.emailService.sendEmail(new AbstractEmailContext() {});
    }
}
