package org.jedi_bachelor.bookstatistic.bookservice.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
@Slf4j
@Data
public class KeycloakTokenService {
    @Value("${keycloak.auth-server-url:http://localhost:8080}")
    private String keycloakUrl;

    @Value("${keycloak.realm:bookstatistic-realm}")
    private String realm;

    @Value("${keycloak.client-id:bookstatistic}")
    private String clientId;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    private final RestClient restClient;

    public KeycloakTokenService() {
        this.restClient = RestClient.create();
    }

    public String getAccessToken() {
        try {
            log.info("Keycloak data. Client ID {}, Client secret {}", this.clientId, this.clientSecret);

            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("grant_type", "client_credentials");
            formData.add("client_id", this.clientId);
            formData.add("client_secret", this.clientSecret);

            String tokenUrl = String.format("%s/realms/%s/protocol/openid-connect/token",
                    this.keycloakUrl, this.realm);

            Map<String, Object> response = this.restClient.post()
                    .uri(tokenUrl)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(formData)
                    .retrieve()
                    .body(Map.class);

            String accessToken = (String) response.get("access_token");

            log.info("Access token has been got: {}", accessToken);

            return accessToken;
        } catch (Exception e) {
            throw new RuntimeException("Failed to obtain access token from Keycloak", e);
        }
    }
}
