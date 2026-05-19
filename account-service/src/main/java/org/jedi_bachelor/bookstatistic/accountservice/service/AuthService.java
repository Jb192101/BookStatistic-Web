package org.jedi_bachelor.bookstatistic.accountservice.service;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.accountservice.dto.JwtResponse;
import org.jedi_bachelor.bookstatistic.accountservice.entity.UserProfile;
import org.jedi_bachelor.bookstatistic.accountservice.repository.UserRepository;
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

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final Keycloak keycloakAdmin;

    private final RestTemplate restTemplate;

    @Value("${spring.security.oauth2.client.provider.keycloak.issuer-uri}")
    private String issuerUri;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-secret}")
    private String clientSecret;

    @Value("${keycloak.admin.realm}")
    private String realm;

    public JwtResponse login(LoginDto loginDto) {
        try {
            String tokenResponse = this.getKeycloakTokens(
                    loginDto.username(),
                    loginDto.password()
            );

            return JwtResponse.builder()
                    .accessToken(this.extractAccessToken(tokenResponse))
                    .refreshToken(this.extractRefreshToken(tokenResponse))
                    .tokenType("Bearer")
                    .expiresIn(this.extractExpiresIn(tokenResponse))
                    .build();

        } catch (Exception e) {
            log.error("Login failed for user: {}", loginDto.username(), e);
            throw new RuntimeException("Invalid credentials");
        }
    }

    public UserProfile register(RegisterDto registerDto) {
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
            userProfile.setHashPassword(passwordEncoder.encode(registerDto.password()));
            userProfile.setLanguage("EN");

            return userRepository.save(userProfile);

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
            this.restTemplate.postForEntity(logoutUrl, request, String.class);
        } catch (Exception e) {
            log.warn("Logout failed: {}", e.getMessage());
        }
    }

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

        return this.parseTokenResponse(response.getBody());
    }

    private void assignDefaultRole(String userId) {
        UsersResource usersResource = this.keycloakAdmin.realm(this.realm).users();

        RoleRepresentation userRole = keycloakAdmin.realm(realm)
                .roles()
                .get("USER")
                .toRepresentation();

        usersResource.get(userId).roles().realmLevel()
                .add(List.of(userRole));
    }

    private String extractUserIdFromResponse(Response response) {
        String location = response.getLocation().toString();
        return location.substring(location.lastIndexOf("/") + 1);
    }

    private String extractAccessToken(String tokenResponse) {
        return tokenResponse.split("\"access_token\":\"")[1].split("\"")[0];
    }

    private String extractRefreshToken(String tokenResponse) {
        return tokenResponse.split("\"refresh_token\":\"")[1].split("\"")[0];
    }

    private long extractExpiresIn(String tokenResponse) {
        String expiresStr = tokenResponse.split("\"expires_in\":")[1].split(",")[0];
        return Long.parseLong(expiresStr);
    }

    private JwtResponse parseTokenResponse(String response) {
        // Реализуйте парсинг JSON через ObjectMapper
        // Временно заглушка
        return JwtResponse.builder().build();
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
