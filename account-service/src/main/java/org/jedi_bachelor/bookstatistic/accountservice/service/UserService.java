package org.jedi_bachelor.bookstatistic.accountservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.accountservice.converter.RegistrationConverter;
import org.jedi_bachelor.bookstatistic.accountservice.dto.JwtResponse;
import org.jedi_bachelor.bookstatistic.accountservice.entity.UserProfile;
import org.jedi_bachelor.bookstatistic.accountservice.language.Language;
import org.jedi_bachelor.bookstatistic.accountservice.mapper.UserMapper;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.account.LoginDto;
import org.keycloak.representations.idm.CredentialRepresentation;
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
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final Keycloak keycloakAdmin;

    private final RestTemplate restTemplate = new RestTemplate();

    private final RegistrationConverter registrationConverter;

    private final OutboxContentManager outboxContentManager;

    private final ObjectMapper objectMapper;

    private final PasswordEncoder passwordEncoder;

    @Value("${spring.security.oauth2.client.provider.keycloak.issuer-uri}")
    private String issuerUri;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-secret}")
    private String clientSecret;

    @Value("${keycloak.realm}")
    private String realm;

    /**
     * Метод получения всех профилей пользователей
     *
     * @return список пользователей
     */
    @Transactional(rollbackOn = Exception.class)
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
    @Transactional(rollbackOn = Exception.class)
    public UserDto getUserById(UUID userId) throws UserNotFoundException {
        Optional<UserProfile> profile = this.userRepository.findById(userId);

        if(profile.isEmpty()) {
            throw new UserNotFoundException(userId);
        }

        return this.userMapper.toDto(profile.get());
    }

    @Transactional(rollbackOn = Exception.class)
    public JwtResponse login(LoginDto loginDto) {
        try {
            String tokenResponse = this.getKeycloakTokens(loginDto.username(), loginDto.password());
            return this.parseTokenResponse(tokenResponse);
        } catch (Exception e) {
            log.error("Login failed for user: {}", loginDto.username(), e);
            throw new RuntimeException("Invalid credentials");
        }
    }

    @Transactional(rollbackOn = Exception.class)
    public void logout(String refreshToken) {
        String logoutUrl = this.issuerUri + "/protocol/openid-connect/logout";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", this.clientId);
        params.add("client_secret", this.clientSecret);
        params.add("refresh_token", refreshToken);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            this.restTemplate.postForEntity(logoutUrl, request, String.class);
            log.info("User logged out successfully");
        } catch (Exception e) {
            log.warn("Logout failed: {}", e.getMessage());
        }
    }

    @Transactional(rollbackOn = Exception.class)
    public JwtResponse refreshToken(String refreshToken) {
        String tokenUrl = this.issuerUri + "/protocol/openid-connect/token";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", this.clientId);
        params.add("client_secret", this.clientSecret);
        params.add("refresh_token", refreshToken);
        params.add("grant_type", "refresh_token");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = this.restTemplate.postForEntity(tokenUrl, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return parseTokenResponse(response.getBody());
        }

        throw new RuntimeException("Failed to refresh token");
    }

    /**
     * Метод регистрации пользователя
     *
     * @return новый профиль
     */
    @Transactional(rollbackOn = Exception.class)
    public UserDto register(RegisterDto dto) throws UserAlreadyExistsInSystemException, PasswordInvalidException {
        if(this.userRepository.findByUsername(dto.username()).isPresent()) {
            throw new UserAlreadyExistsInSystemException(dto.username());
        }

        if(!Objects.equals(dto.password(), dto.confirmPassword())) {
            throw new PasswordInvalidException(dto.password(), dto.confirmPassword());
        }

        UserRepresentation keycloakUser = this.createKeycloakUser(dto);

        // Назначение keycloak-sub
        try (Response response = this.keycloakAdmin.realm(this.realm).users().create(keycloakUser)) {

            if (response.getStatus() != 201) {
                throw new RuntimeException("Failed to create user in Keycloak: " + response.getStatusInfo());
            }

            String userId = this.extractUserIdFromResponse(response);
            this.assignDefaultRole(userId);

            UserProfile userProfile = this.registrationConverter.convert(dto);
            userProfile.setKeycloakSub(userId);

            this.keycloakAdmin.realm(this.realm)
                    .users()
                    .get(userProfile.getKeycloakSub())
                    .sendVerifyEmail();

            UserProfile savedProfile = this.userRepository.save(userProfile);

            // Отправка сообщений в outbox
            OutboxNotificationSettingsMessage message = new OutboxNotificationSettingsMessage();
            message.setUserId(savedProfile.getId());
            message.setOperation(OutboxOperation.ADD_OPERATION);
            message.setEnableBroadcast(dto.enableBroadcast());
            message.setEmailEnable(dto.enableEmail());
            message.setEmailAddress(dto.email());

            this.outboxContentManager.save(message);

            return this.userMapper.toDto(savedProfile);
        } catch (Exception e) {
            log.error("Registration failed", e);
            throw new RuntimeException("Registration failed: " + e.getMessage());
        }
    }

    /**
     * Метод удаления пользователя по ID
     *
     * @param userId ID пользователя
     * @return удалённого пользователя
     */
    @Transactional(rollbackOn = Exception.class)
    public UserDto deleteUser(UUID userId) throws UserNotFoundException {
        UserProfile profile = this.userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        String keycloakId = profile.getKeycloakSub();

        try {
            this.keycloakAdmin.realm(this.realm).users().delete(keycloakId);

            log.info("User deleted from Keycloak: {}", keycloakId);
        } catch (Exception e) {
            log.error("Failed to delete user from Keycloak: {}", keycloakId, e);

            throw new RuntimeException("Failed to delete user from Keycloak: " + e.getMessage());
        }

        this.userRepository.deleteById(userId);

        log.info("User deleted from database: {}", userId);

        return this.userMapper.toDto(profile);
    }

    /**
     * Метод обновления пользователя по DTO
     *
     * @param dto DTO обновления
     * @throws UserNotFoundException если пользователя с ID нет
     * @return пользователя с новыми данными
     */
    @Transactional(rollbackOn = Exception.class)
    public UserDto updateUser(UUID id, UserUpdateDto dto) throws UserNotFoundException, UsernameAlreadyExistsException, PasswordInvalidException {
        if(!this.userRepository.existsById(id)) {
            log.error("User with ID {} doesn't exists", id);

            throw new UserNotFoundException(id);
        }

        // Проверка username-ов
        UserProfile currentProfile = this.userRepository.findById(id).get();
        String currentUsername = currentProfile.getUsername();

        List<String> usernames = new ArrayList<>(this.userRepository.findAll()
                .stream().map(UserProfile::getUsername)
                .toList());

        usernames.remove(currentUsername);

        if(usernames.contains(dto.username())) {
            throw new UsernameAlreadyExistsException(dto.username());
        }

        if(!Objects.equals(dto.confirmPassword(), dto.password())) {
            throw new PasswordInvalidException(dto.password(), dto.confirmPassword());
        }

        // Изменения в Keycloak
        try {
            // Изменение данных
            currentProfile.setUsername(dto.username());
            currentProfile.setFirstName(dto.firstName());
            currentProfile.setMiddleName(dto.middleName());
            currentProfile.setLastName(dto.lastName());
            currentProfile.setLanguage(Language.valueOf(dto.language()));
            currentProfile.setPassword(this.passwordEncoder.encode(dto.password()));
            currentProfile.setBirthDay(dto.birthDay());

            UserProfile savedProfile = this.userRepository.save(currentProfile);

            log.info("User with id {} updated in database", id);

            this.updateKeycloakUser(savedProfile, dto);
        } catch (Exception e) {
            log.error("Failed to update user in Keycloak: {}", id, e);
        }

        log.info("User with id {} has been updated", id);

        return this.userMapper.toDto(currentProfile);
    }

    /**
     * Метод получения токенов из Keycloak
     *
     * @param username username пользователя
     * @param password пароль пользователя
     * @return access token
     */
    private String getKeycloakTokens(String username, String password) {
        String tokenUrl = issuerUri + "/protocol/openid-connect/token";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("username", username);
        params.add("password", password);
        params.add("grant_type", "password");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = this.restTemplate.postForEntity(tokenUrl, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new RuntimeException("Failed to get tokens from Keycloak");
        }
    }

    /**
     * Метод извлечения UserId из ответа
     */
    private String extractUserIdFromResponse(Response response) {
        String location = response.getLocation().toString();
        return location.substring(location.lastIndexOf("/") + 1);
    }

    /**
     * Метод назначения дефолтной роли для пользователя по ID
     */
    private void assignDefaultRole(String userId) {
        UsersResource usersResource = this.keycloakAdmin.realm(this.realm).users();

        RoleRepresentation userRole = this.keycloakAdmin.realm(this.realm)
                .roles()
                .get("ROLE_USER")
                .toRepresentation();

        usersResource.get(userId).roles().realmLevel().add(List.of(userRole));
        log.info("Assigned USER role to user with ID: {}", userId);
    }

    /**
     * Создание пользователя в Keycloak
     */
    private UserRepresentation createKeycloakUser(RegisterDto registerDto) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(registerDto.username());
        user.setLastName(registerDto.lastName());
        user.setFirstName(registerDto.firstName());
        user.setEmail(registerDto.email());
        user.setEnabled(true);

        // Пока будет true, потом надо добавить верификацию по почте
        user.setEmailVerified(false);

        List<String> requiredActions = new ArrayList<>();
        requiredActions.add("VERIFY_EMAIL");
        user.setRequiredActions(requiredActions);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(registerDto.password());
        credential.setTemporary(false);

        user.setCredentials(List.of(credential));

        return user;
    }

    /**
     * Метод парсинга строки в Jwt
     */
    private JwtResponse parseTokenResponse(String responseBody) {
        try {
            JsonNode json = this.objectMapper.readTree(responseBody);

            return JwtResponse.builder()
                    .accessToken(json.get("access_token").asText())
                    .refreshToken(json.get("refresh_token").asText())
                    .tokenType(json.get("token_type").asText())
                    .expiresIn(json.get("expires_in").asLong())
                    .scope(json.has("scope") ? json.get("scope").asText() : null)
                    .build();
        } catch (Exception e) {
            log.error("Failed to parse token response", e);
            throw new RuntimeException("Failed to parse token response");
        }
    }

    /**
     * Обновление пользователя в Keycloak
     */
    private void updateKeycloakUser(UserProfile profile, UserUpdateDto dto) {
        String keycloakId = profile.getKeycloakSub();

        try {
            UserRepresentation user = this.keycloakAdmin.realm(this.realm)
                    .users()
                    .get(keycloakId)
                    .toRepresentation();

            log.info("Current user in Keycloak: username={}, firstName={}, lastName={}, email={}",
                    user.getUsername(), user.getFirstName(), user.getLastName(), user.getEmail());

            UserRepresentation updatedUser = new UserRepresentation();

            updatedUser.setId(user.getId());
            updatedUser.setUsername(user.getUsername());
            updatedUser.setEmail(user.getEmail());
            updatedUser.setEnabled(user.isEnabled());
            updatedUser.setEmailVerified(user.isEmailVerified());
            updatedUser.setFirstName(user.getFirstName());
            updatedUser.setLastName(user.getLastName());

            boolean hasChanges = false;

            if (dto.username() != null && !dto.username().isEmpty()) {
                String newUsername = dto.username();
                if (newUsername.length() > 36) {
                    newUsername = newUsername.substring(0, 36);
                    log.warn("Username truncated to 36 chars: {}", newUsername);
                }
                updatedUser.setUsername(newUsername);
                hasChanges = true;
                log.info("Updating username to: {}", newUsername);
            }

            if (dto.firstName() != null && !dto.firstName().isEmpty()) {
                String newFirstName = dto.firstName();
                if (newFirstName.length() > 50) {
                    newFirstName = newFirstName.substring(0, 50);
                    log.warn("FirstName truncated to 50 chars: {}", newFirstName);
                }
                updatedUser.setFirstName(newFirstName);
                hasChanges = true;
                log.info("Updating firstName to: {}", newFirstName);
            }

            if (dto.lastName() != null && !dto.lastName().isEmpty()) {
                String newLastName = dto.lastName();
                if (newLastName.length() > 50) {
                    newLastName = newLastName.substring(0, 50);
                    log.warn("LastName truncated to 50 chars: {}", newLastName);
                }
                updatedUser.setLastName(newLastName);
                hasChanges = true;
                log.info("Updating lastName to: {}", newLastName);
            }

            if (dto.email() != null && !dto.email().isEmpty()) {
                String newEmail = dto.email();
                if (!newEmail.contains("@") || !newEmail.contains(".")) {
                    log.warn("Invalid email format: {}, skipping update", newEmail);
                } else {
                    updatedUser.setEmail(newEmail);
                    updatedUser.setEmailVerified(false);
                    hasChanges = true;
                    log.info("Updating email to: {}, email verification reset", newEmail);
                }
            }

            if (dto.password() != null && !dto.password().isEmpty()) {
                String newPassword = dto.password();
                if (newPassword.length() < 3) {
                    log.warn("Password too short, minimum 3 characters");
                } else {
                    CredentialRepresentation credential = new CredentialRepresentation();
                    credential.setType(CredentialRepresentation.PASSWORD);
                    credential.setValue(newPassword);
                    credential.setTemporary(false);
                    updatedUser.setCredentials(List.of(credential));
                    hasChanges = true;
                    log.info("Updating password");
                }
            }

            if (hasChanges) {
                log.info("Sending update to Keycloak: username={}, email={}, firstName={}, lastName={}",
                        updatedUser.getUsername(), updatedUser.getEmail(),
                        updatedUser.getFirstName(), updatedUser.getLastName());

                this.keycloakAdmin.realm(this.realm)
                        .users()
                        .get(keycloakId)
                        .update(updatedUser);

                log.info("User updated in Keycloak: {}", keycloakId);
            } else {
                log.info("No changes to update in Keycloak for user: {}", keycloakId);
            }

        } catch (NotFoundException e) {
            log.warn("User not found in Keycloak: {}", keycloakId);
        } catch (BadRequestException e) {
            log.error("Bad request when updating user. This usually means invalid data format.");
            log.error("Check: username length, email format, special characters");

            try {
                UserRepresentation currentUser = this.keycloakAdmin.realm(this.realm)
                        .users()
                        .get(keycloakId)
                        .toRepresentation();

                UserRepresentation minimalUpdate = new UserRepresentation();
                minimalUpdate.setUsername(currentUser.getUsername());
                minimalUpdate.setEmail(currentUser.getEmail());
                minimalUpdate.setEnabled(true);
                minimalUpdate.setEmailVerified(currentUser.isEmailVerified());
                minimalUpdate.setFirstName(currentUser.getFirstName());
                minimalUpdate.setLastName(currentUser.getLastName());

                this.keycloakAdmin.realm(this.realm)
                        .users()
                        .get(keycloakId)
                        .update(minimalUpdate);

                log.info("User updated in Keycloak (without password): {}", keycloakId);
            } catch (Exception ex) {
                log.error("Even minimal update failed: {}", ex.getMessage());
                throw new RuntimeException("Failed to update user in Keycloak: " + e.getMessage());
            }
        } catch (Exception e) {
            log.error("Failed to update user in Keycloak: {}", keycloakId, e);
            throw new RuntimeException("Failed to update user in Keycloak: " + e.getMessage());
        }
    }
}
