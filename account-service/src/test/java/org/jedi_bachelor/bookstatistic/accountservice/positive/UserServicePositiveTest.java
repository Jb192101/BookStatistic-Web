package org.jedi_bachelor.bookstatistic.accountservice.positive;

import org.jedi_bachelor.bookstatistic.accountservice.converter.RegistrationConverter;
import org.jedi_bachelor.bookstatistic.accountservice.entity.UserProfile;
import org.jedi_bachelor.bookstatistic.accountservice.language.Language;
import org.jedi_bachelor.bookstatistic.accountservice.mapper.UserMapper;
import org.jedi_bachelor.bookstatistic.accountservice.outbox.OutboxContentManager;
import org.jedi_bachelor.bookstatistic.accountservice.outbox.entity.OutboxNotificationSettingsMessage;
import org.jedi_bachelor.bookstatistic.accountservice.repository.UserRepository;
import org.jedi_bachelor.bookstatistic.accountservice.service.UserService;
import org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities.UserDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.account.RegisterDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.account.UserUpdateDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Позитивные тесты UserService")
class UserServicePositiveTest {
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
    private UserDto testUserDto;
    private RegisterDto registerDto;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();

        testProfile = new UserProfile();
        testProfile.setId(testUserId);
        testProfile.setUsername("testuser");
        testProfile.setFirstName("Test");
        testProfile.setLastName("User");
        testProfile.setLanguage(Language.EN);
        testProfile.setPassword("encodedPassword");

        testUserDto = new UserDto(
                testUserId,
                "testuser",
                "Test",
                "User",
                "",
                "Test LastName",
                "EN",
                LocalDateTime.now(),
                LocalDate.of(2000, 2, 2)
        );

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
    @DisplayName("getAllProfiles()")
    class GetAllProfilesTests {
        @Test
        @DisplayName("Должен вернуть список всех пользователей")
        void shouldReturnAllProfiles() {
            List<UserProfile> profiles = List.of(testProfile);
            List<UserDto> expectedDtos = List.of(testUserDto);

            when(userRepository.findAll()).thenReturn(profiles);
            when(userMapper.toDtoList(profiles)).thenReturn(expectedDtos);

            List<UserDto> result = userService.getAllProfiles();

            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo(testUserDto);
            verify(userRepository).findAll();
            verify(userMapper).toDtoList(profiles);
        }

        @Test
        @DisplayName("Должен вернуть пустой список, если пользователей нет")
        void shouldReturnEmptyListWhenNoUsers() {
            when(userRepository.findAll()).thenReturn(List.of());
            when(userMapper.toDtoList(List.of())).thenReturn(List.of());

            List<UserDto> result = userService.getAllProfiles();

            assertThat(result).isEmpty();
            verify(userRepository).findAll();
        }
    }

    @Nested
    @DisplayName("getUserById()")
    class GetUserByIdTests {
        @Test
        @DisplayName("Должен вернуть пользователя по ID")
        void shouldReturnUserById() throws Exception {
            when(userRepository.findById(testUserId)).thenReturn(Optional.of(testProfile));
            when(userMapper.toDto(testProfile)).thenReturn(testUserDto);

            UserDto result = userService.getUserById(testUserId);

            assertThat(result).isEqualTo(testUserDto);
            verify(userRepository).findById(testUserId);
            verify(userMapper).toDto(testProfile);
        }
    }

    @Nested
    @DisplayName("register()")
    class RegisterTests {

        @Test
        @DisplayName("Должен успешно зарегистрировать пользователя и создать Outbox-сообщение")
        void shouldRegisterUser() throws Exception {
            when(userRepository.findByUsername(registerDto.username()))
                    .thenReturn(Optional.empty());
            when(registrationConverter.convert(registerDto)).thenReturn(testProfile);
            when(userRepository.save(testProfile)).thenReturn(testProfile);
            when(userMapper.toDto(testProfile)).thenReturn(testUserDto);

            UserDto result = userService.register(registerDto);

            assertThat(result).isEqualTo(testUserDto);

            // Проверяем Outbox-сообщение
            ArgumentCaptor<OutboxNotificationSettingsMessage> messageCaptor =
                    ArgumentCaptor.forClass(OutboxNotificationSettingsMessage.class);
            verify(outboxContentManager).save(messageCaptor.capture());

            OutboxNotificationSettingsMessage savedMessage = messageCaptor.getValue();
            assertThat(savedMessage.getUserId()).isEqualTo(testUserId);
            assertThat(savedMessage.getEmailAddress()).isEqualTo("john@example.com");

            verify(userRepository).findByUsername(registerDto.username());
            verify(registrationConverter).convert(registerDto);
            verify(userRepository).save(testProfile);
            verify(userMapper).toDto(testProfile);
        }
    }

    @Nested
    @DisplayName("deleteUser()")
    class DeleteUserTests {
        @Test
        @DisplayName("Должен успешно удалить пользователя")
        void shouldDeleteUser() throws Exception {
            when(userRepository.existsById(testUserId)).thenReturn(true);
            when(userRepository.findById(testUserId)).thenReturn(Optional.of(testProfile));
            when(userMapper.toDto(testProfile)).thenReturn(testUserDto);

            UserDto result = userService.deleteUser(testUserId);

            assertThat(result).isEqualTo(testUserDto);
            verify(userRepository).existsById(testUserId);
            verify(userRepository).findById(testUserId);
            verify(userRepository).deleteById(testUserId);
            verify(userMapper).toDto(testProfile);
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
            existingProfile.setFirstName("Test");
            existingProfile.setLastName("User");
            existingProfile.setLanguage(Language.EN);
            existingProfile.setPassword("oldEncodedPassword");
        }

        @Test
        @DisplayName("Должен успешно обновить пользователя")
        void shouldUpdateUser() throws Exception {
            when(userRepository.existsById(testUserId)).thenReturn(true);
            when(userRepository.findById(testUserId)).thenReturn(Optional.of(existingProfile));
            when(userRepository.findAll()).thenReturn(List.of(existingProfile));
            when(passwordEncoder.encode("newPassword123")).thenReturn("newEncodedPassword");
            when(userRepository.save(existingProfile)).thenReturn(existingProfile);
            when(userMapper.toDto(existingProfile)).thenReturn(testUserDto);

            UserDto result = userService.updateUser(testUserId, updateDto);

            assertThat(result).isEqualTo(testUserDto);

            // Проверяем, что поля обновились
            assertThat(existingProfile.getUsername()).isEqualTo("updatedUser");
            assertThat(existingProfile.getFirstName()).isEqualTo("Updated");
            assertThat(existingProfile.getLastName()).isEqualTo("User");
            assertThat(existingProfile.getLanguage()).isEqualTo(Language.EN);
            assertThat(existingProfile.getPassword()).isEqualTo("newEncodedPassword");

            verify(userRepository).existsById(testUserId);
            verify(userRepository).findById(testUserId);
            verify(userRepository).findAll();
            verify(passwordEncoder).encode("newPassword123");
            verify(userRepository).save(existingProfile);
        }
    }
}
