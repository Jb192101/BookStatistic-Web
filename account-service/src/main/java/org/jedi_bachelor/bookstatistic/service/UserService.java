package org.jedi_bachelor.bookstatistic.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.bookstatistic.converter.UserConverter;
import org.jedi_bachelor.bookstatistic.dto.mapentities.UserDto;
import org.jedi_bachelor.bookstatistic.dto.request.account.RegisterDto;
import org.jedi_bachelor.bookstatistic.entity.User;
import org.jedi_bachelor.bookstatistic.entity.UserRole;
import org.jedi_bachelor.bookstatistic.exceptions.UserNotFoundException;
import org.jedi_bachelor.bookstatistic.internalinteraction.InteractionClient;
import org.jedi_bachelor.bookstatistic.mapper.UserMapper;
import org.jedi_bachelor.bookstatistic.outbox.OutboxContextManager;
import org.jedi_bachelor.bookstatistic.repository.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
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
        Optional<User> user = this.userRepository.findById(id);

        if(user.isEmpty()) {
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
        List<User> users = this.userRepository.findAll();

        return this.userMapper.toDtoList(users);
    }

    /**
     * Метод для добавления нового пользователя через DTO
     * @param dto DTO для добавления
     * @return DTO с данными созданного пользователя
     */
    @Transactional
    public UserDto addNewUser(RegisterDto dto) {
        User user = this.userConverter.convert(dto);

        this.userRepository.save(user);

        return this.userMapper.toDto(user);
    }

    /**
     * Метод обновления пользователя по DTO
     * @param dto DTO обновления
     * @throws UserNotFoundException если пользователя с ID нет
     * @return пользователя с новыми данными
     */
    @Transactional
    public UserDto updateUser(UserDto dto) throws UserNotFoundException {
        Optional<User> user = this.userRepository.findById(dto.id());

        if(user.isEmpty()) {
            throw new UserNotFoundException(dto.id());
        }

        // Изменения
        user.get().setEmail(dto.email());
        user.get().setRole(UserRole.valueOf(dto.role()));
        user.get().setName(dto.name());
        user.get().setEnableEmail(dto.enableEmail());
        user.get().setHashPassword(dto.hashPassword());
        user.get().setTelegramAddress(dto.telegramAddress());

        this.userRepository.save(user.get());

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
        Optional<User> user = this.userRepository.findById(id);

        // Проверка, если пользователя нет (тогда останавливаем удаление)
        if(user.isEmpty()) {
            throw new UserNotFoundException(id);
        }

        // 2. Удаление в analyze-service
        this.outboxContextManager.addAnalyzeMessageToDelete(id);

        // 3. Удаление в book-service
        this.outboxContextManager.addBookMessageToDelete(id);

        // Возвращение удалённого пользователя
        return this.userMapper.toDto(user.get());
    }

    /**
     * Метод получения всех email-адресов
     *
     * @return список email-адресов
     */
    @Transactional
    public List<String> getEmailAddresses() {
        List<String> result = new ArrayList<>();

        List<User> users = this.userRepository.findAll();

        for(User user : users) {
            if(user.getEnableEmail() == null || user.getEnableEmail() == false) {
                continue;
            }

            result.add(user.getEmail());
        }

        return result;
    }
}
