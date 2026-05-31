package org.jedi_bachelor.bookstatistic.accountservice.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.accountservice.converter.UserConverter;
import org.jedi_bachelor.bookstatistic.accountservice.entity.UserProfile;
import org.jedi_bachelor.bookstatistic.accountservice.mapper.UserMapper;
import org.jedi_bachelor.bookstatistic.accountservice.outbox.OutboxContextManager;
import org.jedi_bachelor.bookstatistic.accountservice.outbox.OutboxOperation;
import org.jedi_bachelor.bookstatistic.accountservice.outbox.entity.OutboxAnalyzeMessage;
import org.jedi_bachelor.bookstatistic.accountservice.outbox.entity.OutboxNotificationSettingsMessage;
import org.jedi_bachelor.bookstatistic.accountservice.repository.UserRepository;
import org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities.UserDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.account.RegisterDto;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.UserNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final UserConverter userConverter;

    private final OutboxContextManager outboxContextManager;

    /**
     * Конструктор класса
     *
     * @param userRepository репозиторий JPA
     * @param userMapper маппер (mapstruct)
     * @param userConverter конвертер DTO в Entity
     * @param outboxContextManager менеджер контекста outbox-сообщений
     */
    public UserService(UserRepository userRepository,
                       UserMapper userMapper,
                       UserConverter userConverter,
                       OutboxContextManager outboxContextManager) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.userConverter = userConverter;
        this.outboxContextManager = outboxContextManager;
    }

    /**
     * Метод для поиска пользователя по ID
     *
     * @param id ID пользователя
     * @return пользователя, если он есть
     * @throws UserNotFoundException исключение, если пользователя нет
     */
    @Transactional
    public UserDto findUserById(UUID id) throws UserNotFoundException {
        Optional<UserProfile> user = this.userRepository.findById(id);

        if(user.isEmpty()) {
            log.error("User with id {} don't exists", id);

            throw new UserNotFoundException(id);
        }

        return this.userMapper.toDto(user.get());
    }

    /**
     * Метод для выдачи всех пользователей
     *
     * @return список пользователей
     */
    @Transactional
    public List<UserDto> findAll() {
        List<UserProfile> userProfiles = this.userRepository.findAll();

        return this.userMapper.toDtoList(userProfiles);
    }

    /**
     * Метод для добавления нового пользователя через DTO
     *
     * @param dto DTO для добавления
     * @return DTO с данными созданного пользователя
     */
    @Transactional
    public UserDto addNewUser(RegisterDto dto) {
        UserProfile userProfile = this.userConverter.convert(dto);

        log.info("New user has created by DTO {}", dto);

        // Отправить задачу в outbox на создание представления пользователя в других системах
        // Предоставление в notification-service
        OutboxNotificationSettingsMessage notificationSettingsMessage =
                new OutboxNotificationSettingsMessage();
        notificationSettingsMessage.setUserId(userProfile.getId());
        notificationSettingsMessage.setOperation(OutboxOperation.ADD_OPERATION);
        notificationSettingsMessage.setEmailAddress(dto.email());
        notificationSettingsMessage.setEmailEnable(dto.enableEmail());

        this.outboxContextManager.save(notificationSettingsMessage);

        // Представление в book-service
        OutboxBookMessage bookMessage = new OutboxBookMessage();
        bookMessage.setAction(OutboxOperation.ADD_OPERATION);
        bookMessage.setUserId(userProfile.getId());

        this.outboxContextManager.save(bookMessage);

        // Представление в analyze-service
        OutboxAnalyzeMessage analyzeMessage = new OutboxAnalyzeMessage();
        analyzeMessage.setUserId(userProfile.getId());
        analyzeMessage.setAction(OutboxOperation.ADD_OPERATION);

        this.outboxContextManager.save(analyzeMessage);

        return this.addNewUser(userProfile);
    }

    @Transactional
    public UserDto addNewUser(UserProfile profile) {
        this.userRepository.save(profile);

        log.info("New user has created by {}", profile);

        return this.userMapper.toDto(profile);
    }

    /**
     * Метод обновления пользователя по DTO
     * @param dto DTO обновления
     * @throws UserNotFoundException если пользователя с ID нет
     * @return пользователя с новыми данными
     */
    @Transactional
    public UserDto updateUser(UserDto dto) throws UserNotFoundException {
        Optional<UserProfile> user = this.userRepository.findById(dto.id());

        if(user.isEmpty()) {
            log.error("User with id {} don't exists", dto.id());

            throw new UserNotFoundException(dto.id());
        }

        // Изменения
        // Для некоторых изменений потом предусмотри перенос в notification-service
        //user.get().setEmail(dto.email());
        user.get().setName(dto.name());
        //user.get().setEnableEmail(dto.enableEmail());
        user.get().setHashPassword(dto.hashPassword());
        //user.get().setTelegramAddress(dto.telegramAddress());

        this.userRepository.save(user.get());

        log.info("User with id {} has been updated", dto.id());

        return this.userMapper.toDto(user.get());
    }

    /**
     * Метод для удаления пользователя
     * Должен каскадно удалять следующие зависимости:
     * - в analyze-service
     * - в book-service
     *
     * @param id ID пользователя
     * @return удалённый пользователь
     * @throws UserNotFoundException если пользователя нет
     */
    @Transactional
    public UserDto deleteUser(UUID id) throws UserNotFoundException {
        // 1. Удаление самого пользователя
        Optional<UserProfile> user = this.userRepository.findById(id);

        // Проверка, если пользователя нет (тогда останавливаем удаление)
        if(user.isEmpty()) {
            throw new UserNotFoundException(id);
        }

        // 2. Удаление в analyze-service
        this.outboxContextManager.addAnalyzeMessageToDelete(id);

        log.info("Sended message to delete user' data with id {} from analyze-service", id);

        // 3. Удаление в book-service
        this.outboxContextManager.addBookMessageToDelete(id);

        log.info("Sended message to delete user' data with id {} from book-service", id);

        // Возвращение удалённого пользователя
        return this.userMapper.toDto(user.get());
    }
}
