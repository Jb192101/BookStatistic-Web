package org.jedi_bachelor.bookstatistic.accountservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.accountservice.converter.RegistrationConverter;
import org.jedi_bachelor.bookstatistic.accountservice.entity.UserProfile;
import org.jedi_bachelor.bookstatistic.accountservice.language.Language;
import org.jedi_bachelor.bookstatistic.accountservice.mapper.UserMapper;
import org.jedi_bachelor.bookstatistic.accountservice.outbox.OutboxContentManager;
import org.jedi_bachelor.bookstatistic.accountservice.outbox.entity.OutboxNotificationSettingsMessage;
import org.jedi_bachelor.bookstatistic.accountservice.outbox.entity.OutboxOperation;
import org.jedi_bachelor.bookstatistic.accountservice.repository.UserRepository;
import org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities.UserDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.account.RegisterDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.account.UserUpdateDto;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.PasswordInvalidException;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.UserAlreadyExistsInSystemException;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.UserNotFoundException;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.UsernameAlreadyExistsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final RegistrationConverter registrationConverter;

    private final OutboxContentManager outboxContentManager;

    private final PasswordEncoder passwordEncoder;

    /**
     * Метод получения всех профилей пользователей
     *
     * @return список пользователей
     */
    @Transactional
    public List<UserDto> getAllProfiles() {
        List<UserProfile> profiles = this.userRepository.findAll();

        return this.userMapper.toDtoList(profiles);
    }

    /**
     * Получение пользователя по ID
     *
     * @param userId ID пользователя
     * @return пользователя, если он есть
     */
    @Transactional
    public UserDto getUserById(UUID userId) throws UserNotFoundException {
        Optional<UserProfile> profile = this.userRepository.findById(userId);

        if(profile.isEmpty()) {
            throw new UserNotFoundException(userId);
        }

        return this.userMapper.toDto(profile.get());
    }

    /**
     * Метод регистрации пользователя
     *
     * @return новый профиль
     */
    @Transactional
    public UserDto register(RegisterDto dto) throws UserAlreadyExistsInSystemException, PasswordInvalidException {
        if(this.userRepository.findByUsername(dto.username()).isPresent()) {
            throw new UserAlreadyExistsInSystemException(dto.username());
        }

        if(!Objects.equals(dto.password(), dto.confirmPassword())) {
            throw new PasswordInvalidException(dto.password(), dto.confirmPassword());
        }

        UserProfile userProfile = this.registrationConverter.convert(dto);

        UserProfile savedProfile = this.userRepository.save(userProfile);

        // Назначение keycloak-sub (потом)


        // Отправка сообщений в outbox
        OutboxNotificationSettingsMessage message = new OutboxNotificationSettingsMessage();
        message.setUserId(savedProfile.getId());
        message.setOperation(OutboxOperation.ADD_OPERATION);
        message.setEnableBroadcast(dto.enableBroadcast());
        message.setEmailEnable(dto.enableEmail());
        message.setEmailAddress(dto.email());

        this.outboxContentManager.save(message);

        return this.userMapper.toDto(savedProfile);
    }

    /**
     * Метод удаления пользователя по ID
     *
     * @param userId ID пользователя
     * @return удалённого пользователя
     */
    @Transactional
    public UserDto deleteUser(UUID userId) throws UserNotFoundException {
        if(!this.userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        UserProfile deletedProfile = this.userRepository.findById(userId).get();

        this.userRepository.deleteById(userId);

        return this.userMapper.toDto(deletedProfile);
    }

    /**
     * Метод обновления пользователя по DTO
     *
     * @param dto DTO обновления
     * @throws UserNotFoundException если пользователя с ID нет
     * @return пользователя с новыми данными
     */
    @Transactional
    public UserDto updateUser(UUID id, UserUpdateDto dto) throws UserNotFoundException, UsernameAlreadyExistsException, PasswordInvalidException {
        if(!this.userRepository.existsById(id)) {
            log.error("User with ID {} doesn't exists", id);

            throw new UserNotFoundException(id);
        }

        // Проверка username-ов
        UserProfile currentProfile = this.userRepository.findById(id).get();
        String currentUsername = currentProfile.getUsername();

        List<String> usernames = new ArrayList<>(this.userRepository.findAll().stream().map(UserProfile::getUsername).toList());
        usernames.remove(currentUsername);

        if(usernames.contains(dto.username())) {
            throw new UsernameAlreadyExistsException(dto.username());
        }

        if(!Objects.equals(dto.confirmPassword(), dto.password())) {
            throw new PasswordInvalidException(dto.password(), dto.confirmPassword());
        }

        // Изменение данных
        currentProfile.setUsername(dto.username());
        currentProfile.setFirstName(dto.firstName());
        currentProfile.setMiddleName(dto.middleName());
        currentProfile.setLastName(dto.lastName());
        currentProfile.setLanguage(Language.valueOf(dto.language()));
        currentProfile.setPassword(this.passwordEncoder.encode(dto.password()));
        currentProfile.setBirthDay(dto.birthDay());

        this.userRepository.save(currentProfile);

        log.info("User with id {} has been updated", id);

        return this.userMapper.toDto(currentProfile);
    }
}
