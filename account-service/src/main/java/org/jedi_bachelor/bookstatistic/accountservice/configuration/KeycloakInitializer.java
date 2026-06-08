package org.jedi_bachelor.bookstatistic.accountservice.configuration;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class KeycloakInitializer {
    private final Keycloak keycloakAdmin;

    @Value("${keycloak.realm:bookstatistic-realm}")
    private String realm;

    @Value("${keycloak.client-id:bookstatistic}")
    private String clientId;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    @PostConstruct
    public void initializeRealm() {
        if (!"${keycloak.init.enabled:true}".equals("true")) {
            log.info("Keycloak initialization is disabled");
            return;
        }

        try {
            List<RealmRepresentation> realms = this.keycloakAdmin.realms().findAll();
            boolean realmExists = realms.stream().anyMatch(r -> r.getRealm().equals(this.realm));

            if (!realmExists) {
                createRealm();
                createRoles();
                createClient();
                log.info("Keycloak realm '{}' initialized successfully", this.realm);
            } else {
                log.info("Keycloak realm '{}' already exists", this.realm);
            }
        } catch (Exception e) {
            log.error("Failed to initialize Keycloak realm", e);
        }
    }

    private void createRealm() {
        RealmRepresentation newRealm = new RealmRepresentation();
        newRealm.setRealm(this.realm);
        newRealm.setEnabled(true);
        newRealm.setDisplayName("BookStatistic Realm");
        newRealm.setSslRequired("EXTERNAL");
        newRealm.setAccessTokenLifespan(300);
        newRealm.setRefreshTokenMaxReuse(1800);

        this.keycloakAdmin.realms().create(newRealm);
        log.info("Realm '{}' created", this.realm);
    }

    private void createRoles() {
        RoleRepresentation userRole = new RoleRepresentation();
        userRole.setName("USER");
        userRole.setDescription("Regular user role");

        RoleRepresentation adminRole = new RoleRepresentation();
        adminRole.setName("ADMIN");
        adminRole.setDescription("Administrator role");

        this.keycloakAdmin.realm(this.realm).roles().create(userRole);
        this.keycloakAdmin.realm(this.realm).roles().create(adminRole);

        log.info("Roles 'USER' and 'ADMIN' created in realm '{}'", this.realm);
    }

    private void createClient() {
        ClientRepresentation client = new ClientRepresentation();
        client.setClientId(this.clientId);
        client.setName("BookStatistic Application");
        client.setEnabled(true);
        client.setPublicClient(false);
        client.setSecret(this.clientSecret);
        client.setStandardFlowEnabled(true);
        client.setDirectAccessGrantsEnabled(true);
        client.setServiceAccountsEnabled(true);
        client.setRedirectUris(List.of(
                "http://localhost:8081/*",
                "http://localhost:8082/*",
                "http://localhost:8083/*",
                "http://localhost:8084/*",
                "http://localhost:8085/*"
        ));

        this.keycloakAdmin.realm(this.realm).clients().create(client);
        log.info("Client '{}' created", this.clientId);
    }
}
