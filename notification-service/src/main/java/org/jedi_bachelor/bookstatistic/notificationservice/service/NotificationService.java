package org.jedi_bachelor.bookstatistic.notificationservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities.NotificationDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities.NotificationSettingsDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.notification.NotificationCreationDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.notification.NotificationSettingsCreatingDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.notification.NotificationSettingsUpdateDto;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.NotificationSettingsNotExistsException;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.UserHaventAccessException;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.UserNotFoundException;
import org.jedi_bachelor.bookstatistic.notificationservice.converter.NotificationConverter;
import org.jedi_bachelor.bookstatistic.notificationservice.email.EmailContext;
import org.jedi_bachelor.bookstatistic.notificationservice.entity.Notification;
import org.jedi_bachelor.bookstatistic.notificationservice.entity.NotificationSettings;
import org.jedi_bachelor.bookstatistic.notificationservice.mapper.NotificationMapper;
import org.jedi_bachelor.bookstatistic.notificationservice.mapper.NotificationSettingsMapper;
import org.jedi_bachelor.bookstatistic.notificationservice.repository.NotificationRepository;
import org.jedi_bachelor.bookstatistic.notificationservice.repository.NotificationSettingsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationMapper notificationMapper;

    private final NotificationRepository notificationRepository;

    private final NotificationSettingsRepository notificationSettingsRepository;

    private final NotificationConverter converter;

    private final NotificationSettingsMapper notificationSettingsMapper;

    private final EmailService emailService;

    @Transactional(rollbackOn = Exception.class)
    public List<NotificationDto> getAllNotifications() {
        return this.notificationMapper.toDtoList(
                this.notificationRepository.findAll()
        );
    }

    /**
     * Метод добавления нового уведомления
     *
     * @param dto DTO создания уведомления
     */
    @Transactional(rollbackOn = Exception.class)
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

        // Отправка в email, если доступно
        if(notificationSettings.get().getEnableEmail()) {
            this.emailService.sendEmail(new EmailContext(
                  notificationSettings.get().getEmail(),
                  dto.notificationTitle(),
                  dto.message()
            ));
        }

        return this.notificationMapper.toDto(notification);
    }

    @Transactional(rollbackOn = Exception.class)
    public void addNotificationSettings(NotificationSettingsCreatingDto dto) throws NotificationSettingsNotExistsException {
        //if(this.notificationSettingsRepository.existsByUserId(dto.userId())) {
        //    throw new NotificationSettingsNotExistsException(dto.userId());
        //}

        NotificationSettings settings = new NotificationSettings();
        settings.setUserId(dto.userId());
        settings.setEmail(dto.emailAddress());
        settings.setEnableEmail(dto.enableEmail());
        settings.setEnableGettingBroadcastMessages(dto.enableBroadcastMessages());
        settings.setTelegramAddress(dto.telegram());

        this.notificationSettingsRepository.save(settings);

        log.info("Notification settings for user with id {} succesfully created", dto.userId());
    }

    @Transactional(rollbackOn = Exception.class)
    public NotificationSettingsDto getNotificationSettings(UUID userId) throws UserNotFoundException, UserHaventAccessException {
        //UUID currentUserId = SecurityUtils.getCurrentUserId();

        //if(!SecurityUtils.hasRole(SecurityRoles.ADMIN.toString())
        //        && currentUserId != userId) {
        //    throw new UserHaventAccessException(currentUserId, userId);
        //}

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
    @Transactional(rollbackOn = Exception.class)
    public void deleteNotificationSettings(UUID userId) throws UserNotFoundException, UserHaventAccessException {
        //UUID currentUserId = SecurityUtils.getCurrentUserId();

        //if(!SecurityUtils.hasRole(SecurityRoles.ADMIN.toString())
        //        && currentUserId != userId) {
        //    throw new UserHaventAccessException(currentUserId, userId);
        //}

        Optional<NotificationSettings> settings = this.notificationSettingsRepository.findByUserId(userId);

        if(settings.isEmpty()) {
            log.error("Notification with id {} did not found", userId);

            throw new UserNotFoundException(userId);
        }

        this.notificationSettingsRepository.delete(settings.get());
    }

    @Transactional(rollbackOn = Exception.class)
    public NotificationSettingsDto updateNotificationSettings(UUID userId, NotificationSettingsUpdateDto dto) throws UserNotFoundException {
        if(!this.notificationSettingsRepository.existsByUserId(userId)) {
            throw new UserNotFoundException(userId);
        }

        NotificationSettings settings = this.notificationSettingsRepository.findByUserId(userId).get();

        settings.setEmail(dto.emailAddress());
        settings.setTelegramAddress(dto.telegram());
        settings.setEnableEmail(dto.enableEmail());
        settings.setEnableGettingBroadcastMessages(dto.enableBroadcastMessages());

        this.notificationSettingsRepository.save(settings);

        return this.notificationSettingsMapper.toDto(settings);
    }
}
