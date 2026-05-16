package org.jedi_bachelor.bookstatistic.accountservice.configuration;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.RolesRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class KeycloakInitializer {
    private final Keycloak keycloakAdmin;

    @Value("${keycloak.admin.realm}")
    private String realm;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
    private String clientId;

    @PostConstruct
    public void initializeRealm() {
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

        newRealm.setSslRequired("external");

        newRealm.setAccessTokenLifespan(300);
        newRealm.setRefreshTokenMaxReuse(1800);

        this.keycloakAdmin.realms().create(newRealm);
        log.info("Realm '{}' created", realm);
    }

    private void createRoles() {
        RolesRepresentation roles = new RolesRepresentation();

        RoleRepresentation adminRole = new RoleRepresentation();
        adminRole.setName("ADMIN");
        adminRole.setDescription("Administrator role");

        RoleRepresentation userRole = new RoleRepresentation();
        userRole.setName("USER");
        userRole.setDescription("Regular user role");

        roles.setRealm(List.of(adminRole, userRole));

        this.keycloakAdmin.realm(this.realm).roles().create((RoleRepresentation) roles.getRealm());
        log.info("Roles created in realm '{}'", this.realm);
    }

    private void createClient() {
        ClientRepresentation client = new ClientRepresentation();
        client.setClientId(clientId);
        client.setName("BookStatistic Application");
        client.setEnabled(true);
        client.setPublicClient(false);
        client.setSecret("your-client-secret");

        client.setStandardFlowEnabled(true);
        client.setDirectAccessGrantsEnabled(true);
        client.setServiceAccountsEnabled(true);

        client.setRedirectUris(List.of("http://localhost:8081/*", "http://localhost:8082/*"));

        this.keycloakAdmin.realm(this.realm).clients().create(client);
        log.info("Client '{}' created", this.clientId);
    }
}
