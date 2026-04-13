package org.jedi_bachelor.bookstatistic.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.bookstatistic.converter.UserConverter;
import org.jedi_bachelor.bookstatistic.dto.mapentities.UserDto;
import org.jedi_bachelor.bookstatistic.dto.request.account.UserCreationDto;
import org.jedi_bachelor.bookstatistic.entity.User;
import org.jedi_bachelor.bookstatistic.exceptions.UserNotFoundException;
import org.jedi_bachelor.bookstatistic.mapper.UserMapper;
import org.jedi_bachelor.bookstatistic.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final UserConverter userConverter;

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
     * Метод для удаления пользователя
     * Должен каскадно удалять следующие зависимости:
     * - в analyze-service
     * - в book-service
     *
     * @param id ID пользователя
     */
    @Transactional
    public void deleteUser(UUID id) throws UserNotFoundException {
        // 1. Удаление самого пользователя
        Optional<User> user = this.userRepository.findById(id);

        // Проверка, если пользователя нет (тогда останавливаем удаление)
        if(user.isEmpty()) {
            throw new UserNotFoundException(id);
        }

        // 2. Удаление в analyze-service

        // 3. Удаление в book-service
    }
}
