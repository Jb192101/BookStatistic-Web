package org.jedi_bachelor.bookstatistic.notificationservice.service;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities.NotificationDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities.NotificationSettingsDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.notification.NotificationCreationDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.notification.NotificationSettingsCreatingDto;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.NotificationNotFoundException;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.NotificationSettingsNotExistsException;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.UserNotFoundException;
import org.jedi_bachelor.bookstatistic.notificationservice.converter.NotificationConverter;
import org.jedi_bachelor.bookstatistic.notificationservice.entity.Notification;
import org.jedi_bachelor.bookstatistic.notificationservice.entity.NotificationSettings;
import org.jedi_bachelor.bookstatistic.notificationservice.entity.enums.NotificationType;
import org.jedi_bachelor.bookstatistic.notificationservice.mapper.NotificationMapper;
import org.jedi_bachelor.bookstatistic.notificationservice.mapper.NotificationSettingsMapper;
import org.jedi_bachelor.bookstatistic.notificationservice.outbox.OutboxContextManager;
import org.jedi_bachelor.bookstatistic.notificationservice.outbox.entity.OutboxEmailMessage;
import org.jedi_bachelor.bookstatistic.notificationservice.outbox.entity.OutboxKafkaMessage;
import org.jedi_bachelor.bookstatistic.notificationservice.repository.NotificationRepository;
import org.jedi_bachelor.bookstatistic.notificationservice.repository.NotificationSettingsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepository;

    private final NotificationMapper notificationMapper;

    private final NotificationSettingsMapper notificationSettingsMapper;

    private final NotificationSettingsRepository notificationSettingsRepository;

    private final NotificationConverter converter;

    private final OutboxContextManager outboxContextManager;

    /**
     * Метод добавления нового уведомления
     *
     * @param dto DTO создания уведомления
     */
    @CircuitBreaker(name = "notification-circuitbreaker")
    @Bulkhead(name = "notification-bulkhead")
    @Transactional
    public NotificationDto addNewNotification(NotificationCreationDto dto) throws NotificationSettingsNotExistsException {
        // Создание нового уведомления
        Notification notification = this.converter.convert(dto);

        // Сохранение
        this.notificationRepository.save(notification);

        // Получение настроек у пользователя
        Optional<NotificationSettings> notificationSettings
                = this.notificationSettingsRepository.findByUserId(notification.getUserId());

        if(notificationSettings.isEmpty()) {
            throw new NotificationSettingsNotExistsException(notification.getUserId());
        }

        // Если настройки удовлетворены, отправляем сообщение в email
        // (OutboxEmailMessage пока не сделан)
        if(notificationSettings.get().getEnableEmail() && notification.getType().isEmail()) {
            this.outboxContextManager.saveOutboxEmailMessage(new OutboxEmailMessage());
        }

        // Отправляем сообщение в Kafka (Outbox)
        // (OutboxKafkaMessage пока не сделан)
        this.outboxContextManager.saveOutboxKafkaMessage(new OutboxKafkaMessage());

        return this.notificationMapper.toDto(notification);
    }

    @CircuitBreaker(name = "notification-circuitbreaker")
    @Bulkhead(name = "notification-bulkhead")
    @Transactional
    public List<NotificationDto> getAllNotifications() {
        return this.notificationMapper.toDtoList(
                this.notificationRepository.findAll()
        );
    }

    @CircuitBreaker(name = "notification-circuitbreaker")
    @Bulkhead(name = "getNotificationOfUser")
    @Transactional
    public List<NotificationDto> getUserNotificationsInSystem(UUID userId) throws UserNotFoundException {
        List<NotificationDto> notifications = this.getUserNotifications(userId);

        return notifications
                .stream()
                .filter(n -> NotificationType.valueOf(n.type()).isSystem())
                .toList();
    }

    @CircuitBreaker(name = "notification-circuitbreaker")
    @Bulkhead(name = "getNotificationOfUser")
    @Transactional
    public List<NotificationDto> getUserNotifications(UUID userId) throws UserNotFoundException {
        List<Notification> dtos = this.notificationRepository.findByUserId(userId);

        if (dtos.isEmpty()) {
            log.error("User with id {} did not found", userId);

            throw new UserNotFoundException(userId);
        }

        return this.notificationMapper.toDtoList(
                dtos
        );
    }

    @CircuitBreaker(name = "notification-circuitbreaker")
    @Bulkhead(name = "notification-bulkhead", type = Bulkhead.Type.THREADPOOL)
    @Transactional
    public NotificationDto getNotification(UUID notificationId) throws NotificationNotFoundException {
        Optional<Notification> notification = this.notificationRepository.findById(notificationId);

        if(notification.isEmpty()) {
            log.error("Notification with id {} did not found", notificationId);

            throw new NotificationNotFoundException(notificationId);
        }

        return this.notificationMapper.toDto(
                notification.get()
        );
    }

    @CircuitBreaker(name = "notification-circuitbreaker")
    @Bulkhead(name = "notification-bulkhead", type = Bulkhead.Type.THREADPOOL)
    @Transactional
    public void deleteNotification(UUID notificationId) throws NotificationNotFoundException {
        Optional<Notification> notification = this.notificationRepository.findById(notificationId);

        if(notification.isEmpty()) {
            log.error("Notification with id {} did not found", notificationId);

            throw new NotificationNotFoundException(notificationId);
        }

        this.notificationRepository.delete(notification.get());
    }

    @CircuitBreaker(name = "notification-circuitbreaker")
    @Bulkhead(name = "notification-bulkhead", type = Bulkhead.Type.THREADPOOL)
    @Transactional
    public void addNotificationSettings(NotificationSettingsCreatingDto dto) {
        NotificationSettings settings = new NotificationSettings();
        settings.setUserId(dto.userId());
        settings.setEmail(dto.emailAddress());
        settings.setEnableEmail(dto.enableEmail());

        this.notificationSettingsRepository.save(settings);

        log.info("Notification settings for user with id {} succesfully created", dto.userId());
    }

    public NotificationSettingsDto getNotificationSettings(UUID userId) throws UserNotFoundException {
        Optional<NotificationSettings> settings = this.notificationSettingsRepository.findByUserId(userId);

        if(settings.isEmpty()) {
            throw new UserNotFoundException(userId);
        }

        return this.notificationSettingsMapper.toDto(settings.get());
    }

    /**
     * Метод удаления настроек уведомлений
     * Используется при удалении пользователя из системы
     *
     * @param userId ID пользователя
     */
    @CircuitBreaker(name = "notification-circuitbreaker")
    @Bulkhead(name = "notification-bulkhead", type = Bulkhead.Type.THREADPOOL)
    @Transactional
    public void deleteNotificationSettings(UUID userId) throws UserNotFoundException {
        Optional<NotificationSettings> settings = this.notificationSettingsRepository.findByUserId(userId);

        if(settings.isEmpty()) {
            log.error("Notification with id {} did not found", userId);

            throw new UserNotFoundException(userId);
        }

        this.notificationSettingsRepository.delete(settings.get());
    }
}
