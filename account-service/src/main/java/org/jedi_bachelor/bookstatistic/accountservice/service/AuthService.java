package org.jedi_bachelor.bookstatistic.accountservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.accountservice.dto.JwtResponse;
import org.jedi_bachelor.bookstatistic.accountservice.entity.UserProfile;
import org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities.UserDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.account.LoginDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.account.RegisterDto;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final Keycloak keycloakAdmin;

    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    @Value("${spring.security.oauth2.client.provider.keycloak.issuer-uri}")
    private String issuerUri;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-secret}")
    private String clientSecret;

    @Value("${keycloak.realm}")
    private String realm;

    public JwtResponse login(LoginDto loginDto) {
        try {
            String tokenResponse = getKeycloakTokens(loginDto.username(), loginDto.password());
            return parseTokenResponse(tokenResponse);
        } catch (Exception e) {
            log.error("Login failed for user: {}", loginDto.username(), e);
            throw new RuntimeException("Invalid credentials");
        }
    }

    @Transactional
    public UserDto register(RegisterDto registerDto) {
        /*
        try {
            List<UserRepresentation> existingUsers = this.keycloakAdmin.realm(this.realm)
                    .users()
                    .search(registerDto.username());

            if (!existingUsers.isEmpty()) {
                throw new RuntimeException("User already exists");
            }
        } catch (Exception e) {
            log.warn("Error checking existing user: {}", e.getMessage());
        }
         */

        UserRepresentation keycloakUser = this.createKeycloakUser(registerDto);

        try (Response response = this.keycloakAdmin.realm(this.realm).users().create(keycloakUser)) {

            if (response.getStatus() != 201) {
                throw new RuntimeException("Failed to create user in Keycloak: " + response.getStatusInfo());
            }

            String userId = this.extractUserIdFromResponse(response);
            this.assignDefaultRole(userId);

            UserProfile userProfile = new UserProfile();
            userProfile.setId(UUID.randomUUID());
            userProfile.setKeycloakSub(userId);
            userProfile.setName(registerDto.username());
            userProfile.setHashPassword(this.passwordEncoder.encode(registerDto.password()));
            userProfile.setLanguage("EN");
            userProfile.setCreatedAt(LocalDateTime.now());
            userProfile.setBirthDay(registerDto.birthDay());

            return this.userService.addNewUser(userProfile, registerDto);
        } catch (Exception e) {
            log.error("Registration failed", e);
            throw new RuntimeException("Registration failed: " + e.getMessage());
        }
    }

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
            restTemplate.postForEntity(logoutUrl, request, String.class);
            log.info("User logged out successfully");
        } catch (Exception e) {
            log.warn("Logout failed: {}", e.getMessage());
        }
    }

    public JwtResponse refreshToken(String refreshToken) {
        String tokenUrl = issuerUri + "/protocol/openid-connect/token";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
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

        ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new RuntimeException("Failed to get tokens from Keycloak");
        }
    }

    private JwtResponse parseTokenResponse(String responseBody) {
        try {
            JsonNode json = objectMapper.readTree(responseBody);

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

    private void assignDefaultRole(String userId) {
        UsersResource usersResource = keycloakAdmin.realm(realm).users();

        RoleRepresentation userRole = keycloakAdmin.realm(realm)
                .roles()
                .get("USER")
                .toRepresentation();

        usersResource.get(userId).roles().realmLevel().add(List.of(userRole));
        log.info("Assigned USER role to user with ID: {}", userId);
    }

    private String extractUserIdFromResponse(Response response) {
        String location = response.getLocation().toString();
        return location.substring(location.lastIndexOf("/") + 1);
    }

    private UserRepresentation createKeycloakUser(RegisterDto registerDto) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(registerDto.username());
        user.setEmail(registerDto.email());
        user.setEnabled(true);
        user.setEmailVerified(false);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(registerDto.password());
        credential.setTemporary(false);
        user.setCredentials(List.of(credential));

        return user;
    }
}