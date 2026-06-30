package org.jedi_bachelor.bookstatistic.accountservice.negative;

import org.jedi_bachelor.bookstatistic.accountservice.converter.RegistrationConverter;
import org.jedi_bachelor.bookstatistic.accountservice.entity.UserProfile;
import org.jedi_bachelor.bookstatistic.accountservice.mapper.UserMapper;
import org.jedi_bachelor.bookstatistic.accountservice.outbox.OutboxContentManager;
import org.jedi_bachelor.bookstatistic.accountservice.repository.UserRepository;
import org.jedi_bachelor.bookstatistic.accountservice.service.UserService;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.account.RegisterDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.account.UserUpdateDto;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.PasswordInvalidException;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.UserAlreadyExistsInSystemException;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.UserNotFoundException;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.UsernameAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Негативные тесты UserService")
class UserServiceNegativeTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private RegistrationConverter registrationConverter;

    @Mock
    private OutboxContentManager outboxContentManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UUID testUserId;
    private UserProfile testProfile;
    private RegisterDto registerDto;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();

        testProfile = new UserProfile();
        testProfile.setId(testUserId);
        testProfile.setUsername("testuser");

        registerDto = new RegisterDto(
                "newuser",
                "password123",
                "password123",
                "John",
                "M",
                "Doe",
                "john@example.com",
                true,
                false,
                "@telegram",
                LocalDate.of(2000, 1, 1),
                "EN"
        );
    }

    @Nested
    @DisplayName("getUserById()")
    class GetUserByIdTests {

        @Test
        @DisplayName("Должен выбросить UserNotFoundException, если пользователь не найден")
        void shouldThrowExceptionWhenUserNotFound() {
            when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getUserById(testUserId))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining(testUserId.toString());

            verify(userRepository).findById(testUserId);
            verify(userMapper, never()).toDto(any());
        }
    }

    @Nested
    @DisplayName("register()")
    class RegisterTests {

        @Test
        @DisplayName("Должен выбросить UserAlreadyExistsInSystemException, если username занят")
        void shouldThrowExceptionWhenUsernameExists() {
            when(userRepository.findByUsername(registerDto.username()))
                    .thenReturn(Optional.of(testProfile));

            assertThatThrownBy(() -> userService.register(registerDto))
                    .isInstanceOf(UserAlreadyExistsInSystemException.class)
                    .hasMessageContaining(registerDto.username());

            verify(userRepository).findByUsername(registerDto.username());
            verify(registrationConverter, never()).convert(any());
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Должен выбросить PasswordInvalidException, если пароли не совпадают")
        void shouldThrowExceptionWhenPasswordsDoNotMatch() {
            RegisterDto invalidDto = new RegisterDto(
                    "newuser",
                    "password123",
                    "differentPassword",
                    "John",
                    "M",
                    "Doe",
                    "john@example.com",
                    true,
                    false,
                    "@telegram",
                    LocalDate.of(2000, 1, 1),
                    "EN"
            );

            assertThatThrownBy(() -> userService.register(invalidDto))
                    .isInstanceOf(PasswordInvalidException.class);

            verify(userRepository, never()).findByUsername(any());
            verify(registrationConverter, never()).convert(any());
            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("deleteUser()")
    class DeleteUserTests {
        @Test
        @DisplayName("Должен выбросить UserNotFoundException, если пользователь не найден")
        void shouldThrowExceptionWhenUserNotFound() {
            when(userRepository.existsById(testUserId)).thenReturn(false);

            assertThatThrownBy(() -> userService.deleteUser(testUserId))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining(testUserId.toString());

            verify(userRepository).existsById(testUserId);
            verify(userRepository, never()).findById(any());
            verify(userRepository, never()).deleteById(any());
        }
    }

    @Nested
    @DisplayName("updateUser()")
    class UpdateUserTests {

        private UserUpdateDto updateDto;
        private UserProfile existingProfile;

        @BeforeEach
        void setUpUpdate() {
            updateDto = new UserUpdateDto(
                    "updatedUser",
                    "newPassword123",
                    "newPassword123",
                    "Updated",
                    "U",
                    "User",
                    "EN",
                    LocalDate.of(2000, 1, 1)
            );

            existingProfile = new UserProfile();
            existingProfile.setId(testUserId);
            existingProfile.setUsername("testuser");
        }

        @Test
        @DisplayName("Должен выбросить UserNotFoundException, если пользователь не найден")
        void shouldThrowExceptionWhenUserNotFound() {
            when(userRepository.existsById(testUserId)).thenReturn(false);

            assertThatThrownBy(() -> userService.updateUser(testUserId, updateDto))
                    .isInstanceOf(UserNotFoundException.class);

            verify(userRepository).existsById(testUserId);
            verify(userRepository, never()).findById(any());
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Должен выбросить UsernameAlreadyExistsException, если username занят другим пользователем")
        void shouldThrowExceptionWhenUsernameExists() {
            UserProfile anotherUser = new UserProfile();
            anotherUser.setId(UUID.randomUUID());
            anotherUser.setUsername("updatedUser");

            List<UserProfile> profiles = List.of(existingProfile, anotherUser);

            when(userRepository.existsById(testUserId)).thenReturn(true);
            when(userRepository.findById(testUserId)).thenReturn(Optional.of(existingProfile));
            when(userRepository.findAll()).thenReturn(profiles);

            assertThatThrownBy(() -> userService.updateUser(testUserId, updateDto))
                    .isInstanceOf(UsernameAlreadyExistsException.class)
                    .hasMessageContaining("updatedUser");

            verify(userRepository).existsById(testUserId);
            verify(userRepository).findById(testUserId);
            verify(userRepository).findAll();
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Должен выбросить PasswordInvalidException, если пароли не совпадают")
        void shouldThrowExceptionWhenPasswordsDoNotMatch() {
            UserUpdateDto invalidDto = new UserUpdateDto(
                    "updatedUser",
                    "newPassword123",
                    "differentPassword",
                    "Updated",
                    "U",
                    "User",
                    "EN",
                    LocalDate.of(2000, 1, 1)
            );

            when(userRepository.existsById(testUserId)).thenReturn(true);
            when(userRepository.findById(testUserId)).thenReturn(Optional.of(existingProfile));

            assertThatThrownBy(() -> userService.updateUser(testUserId, invalidDto))
                    .isInstanceOf(PasswordInvalidException.class);

            verify(userRepository).existsById(testUserId);
            verify(userRepository).findById(testUserId);
            verify(userRepository, never()).findAll();
            verify(userRepository, never()).save(any());
        }
    }
}
