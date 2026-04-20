package org.jedi_bachelor.bookstatistic.service;

import jakarta.transaction.Transactional;
import org.jedi_bachelor.bookstatistic.converter.UserConverter;
import org.jedi_bachelor.bookstatistic.dto.mapentities.UserDto;
import org.jedi_bachelor.bookstatistic.dto.request.account.UserCreationDto;
import org.jedi_bachelor.bookstatistic.entity.User;
import org.jedi_bachelor.bookstatistic.entity.UserRole;
import org.jedi_bachelor.bookstatistic.exceptions.UserNotFoundException;
import org.jedi_bachelor.bookstatistic.internalinteraction.InteractionClient;
import org.jedi_bachelor.bookstatistic.mapper.UserMapper;
import org.jedi_bachelor.bookstatistic.repository.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final UserConverter userConverter;

    private final InteractionClient bookInteractionClient;

    private final InteractionClient analyzerInteractionClient;

    /**
     * Конструктор класса
     *
     * @param userRepository репозиторий JPA
     * @param userMapper маппер (mapstruct)
     * @param userConverter конвертер DTO в Entity
     * @param bookInteractionClient клиент взаимодействия с book-service
     * @param analyzerInteractionClient клиент взаимодействия с analyze-service
     */
    public UserService(UserRepository userRepository,
                       UserMapper userMapper,
                       UserConverter userConverter,
                       @Qualifier("bookInteractionClient") InteractionClient bookInteractionClient,
                       @Qualifier("analyzerInteractionClient") InteractionClient analyzerInteractionClient) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.userConverter = userConverter;
        this.bookInteractionClient = bookInteractionClient;
        this.analyzerInteractionClient = analyzerInteractionClient;
    }

    /**
     * Метод для поиска пользователя по ID
     *
     * @param id ID пользователя
     * @return пользователя, если он есть
     * @throws UserNotFoundException исключение, если пользователя нет
     */
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
    public List<UserDto> findAll() {
        List<User> users = this.userRepository.findAll();

        return this.userMapper.toDtoList(users);
    }

    /**
     * Метод для добавления нового пользователя через DTO
     * @param dto DTO для добавления
     * @return DTO с данными созданного пользователя
     */
    public UserDto addNewUser(UserCreationDto dto) {
        User user = this.userConverter.convert(dto);

        this.userRepository.save(user);

        return this.userMapper.toDto(user);
    }

    /**
     * Метод обновления пользователя по DTO
     * @param dto DTO обновления
     * @return пользователя с новыми данными
     */
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
        this.analyzerInteractionClient.sendRequest(HttpMethod.DELETE, "/" + id);

        // 3. Удаление в book-service
        this.bookInteractionClient.sendRequest(HttpMethod.DELETE, "/" + id);

        // Возвращение удалённого пользователя
        return this.userMapper.toDto(user.get());
    }
}
