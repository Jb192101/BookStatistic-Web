package org.jedi_bachelor.bookstatistic.accountservice.configuration;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class KeycloakAdminConfiguration {

    @Value("${keycloak.admin.server-url:http://keycloak:8080}")
    private String serverUrl;

    @Value("${keycloak.admin.realm:master}")
    private String realm;

    @Value("${keycloak.admin.client-id:admin-cli}")
    private String clientId;

    @Value("${keycloak.admin.client-secret:}")
    private String clientSecret;

    @Value("${keycloak.admin.username:admin}")
    private String username;

    @Value("${keycloak.admin.password:admin}")
    private String password;

    @Bean
    public Keycloak keycloakAdmin() {
        if (this.clientSecret == null || this.clientSecret.isEmpty()) {
            return KeycloakBuilder.builder()
                    .serverUrl(this.serverUrl)
                    .realm(this.realm)
                    .username(this.username)
                    .password(this.password)
                    .clientId(this.clientId)
                    .grantType("password")
                    .build();
        }

        return KeycloakBuilder.builder()
                .serverUrl(this.serverUrl)
                .realm(this.realm)
                .clientId(this.clientId)
                .clientSecret(this.clientSecret)
                .grantType("client_credentials")
                .build();
    }
}