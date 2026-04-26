package org.jedi_bachelor.bookstatistic.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.bookstatistic.converter.NotificationConverter;
import org.jedi_bachelor.bookstatistic.dto.request.notification.NotificationCreationDto;
import org.jedi_bachelor.bookstatistic.dto.mapentities.NotificationDto;
import org.jedi_bachelor.bookstatistic.entity.Notification;
import org.jedi_bachelor.bookstatistic.entity.NotificationSettings;
import org.jedi_bachelor.bookstatistic.exceptions.NotificationNotFoundException;
import org.jedi_bachelor.bookstatistic.exceptions.UserNotFoundException;
import org.jedi_bachelor.bookstatistic.mapper.NotificationMapper;
import org.jedi_bachelor.bookstatistic.outbox.OutboxContextManager;
import org.jedi_bachelor.bookstatistic.outbox.entity.OutboxEmailMessage;
import org.jedi_bachelor.bookstatistic.outbox.entity.OutboxKafkaMessage;
import org.jedi_bachelor.bookstatistic.repository.NotificationRepository;
import org.jedi_bachelor.bookstatistic.repository.NotificationSettingsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    private final NotificationMapper notificationMapper;

    private final NotificationSettingsRepository notificationSettingsRepository;

    private final NotificationConverter converter;

    private final OutboxContextManager outboxContextManager;

    /**
     * Метод добавления нового уведомления
     *
     * @param dto DTO создания уведомления
     */
    @Transactional
    public NotificationDto addNewNotification(NotificationCreationDto dto) {
        // Создание нового уведомления
        Notification notification = this.converter.convert(dto);

        // Сохранение
        this.notificationRepository.save(notification);

        // Получение настроек у пользователя
        NotificationSettings notificationSettings
                = this.notificationSettingsRepository.findByUserId(notification.getUserId());

        // Если настройки удовлетворены, отправляем сообщение в email
        // (OutboxEmailMessage пока не сделан)
        if(notificationSettings.getEmailEnable()) {
            this.outboxContextManager.saveOutboxEmailMessage(new OutboxEmailMessage());
        }

        // Отправляем сообщение в Kafka (Outbox)
        // (OutboxKafkaMessage пока не сделан)
        this.outboxContextManager.saveOutboxKafkaMessage(new OutboxKafkaMessage());

        return this.notificationMapper.toDto(notification);
    }

    @Transactional
    public List<NotificationDto> getAllNotifications() {
        return this.notificationMapper.toDtoList(
                this.notificationRepository.findAll()
        );
    }

    @Transactional
    public List<NotificationDto> getUserNotifications(UUID userId) throws UserNotFoundException {
        List<Notification> dtos = this.notificationRepository.findByUserId(userId);

        if(dtos.isEmpty()) {
            throw new UserNotFoundException(userId);
        }

        return this.notificationMapper.toDtoList(
                dtos
        );
    }

    @Transactional
    public NotificationDto getNotification(UUID notificationId) throws NotificationNotFoundException {
        Optional<Notification> notification = this.notificationRepository.findByNotificationId(notificationId);

        if(notification.isEmpty()) {
            throw new NotificationNotFoundException(notificationId);
        }

        return this.notificationMapper.toDto(
                notification.get()
        );
    }

    @Transactional
    public void deleteNotification(UUID notificationId) throws NotificationNotFoundException {
        Optional<Notification> notification = this.notificationRepository.findByNotificationId(notificationId);

        if(notification.isEmpty()) {
            throw new NotificationNotFoundException(notificationId);
        }

        this.notificationRepository.delete(notification.get());
    }

    @Transactional
    public void addNotificationSettings(UUID userId) {
        NotificationSettings settings = new NotificationSettings(userId);

        this.notificationSettingsRepository.save(settings);
    }

    /**
     * Метод удаления настроек уведомлений
     * Используется при удалении пользователя из системы
     *
     * @param userId ID пользователя
     */
    @Transactional
    public void deleteNotificationSettings(UUID userId) throws UserNotFoundException {
        Optional<NotificationSettings> settings = this.notificationSettingsRepository.findByUserId(userId);

        if(settings.isEmpty()) {
            throw new UserNotFoundException(userId);
        }

        this.notificationSettingsRepository.delete(settings.get());
    }
}
