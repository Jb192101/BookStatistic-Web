package org.jedi_bachelor.bookstatistic.accountservice.configuration;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfiguration {
    @Value("${keycloak.auth-server-url:http://keycloak:8080}")
    private String serverUrl;

    @Value("${keycloak.realm:bookstatistic-realm}")
    private String realm;

    @Value("${keycloak.client-id:bookstatistic}")
    private String clientId;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    @Bean
    public Keycloak keycloakAdmin() {
        return KeycloakBuilder.builder()
                .serverUrl(this.serverUrl)
                .realm(this.realm)
                .clientId(this.clientId)
                .clientSecret(this.clientSecret)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();
    }
}
