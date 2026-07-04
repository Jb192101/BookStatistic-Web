package org.jedi_bachelor.bookstatistic.accountservice.configuration;

/**
 * Класс первичной инициализации данных в Keycloak
 * После запуска создаёт:
 * 1. Realm с названием bookstatistic-realm
 * 2. Формируем роли ROLE_USER, ROLE_ADMIN и ROLE_MODERATOR
 * 3. Создаёт клиента bookstatistic
 */

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Value("${keycloak.smtp.host:smtp.yandex.ru}")
    private String smtpHost;

    @Value("${keycloak.smtp.port:465}")
    private String smtpPort;

    @Value("${keycloak.smtp.from:baryshevgrigorion@yandex.ru}")
    private String smtpFrom;

    @Value("${keycloak.smtp.from-display-name:BookStatistic Team}")
    private String smtpFromDisplayName;

    @Value("${keycloak.smtp.reply-to:baryshevgrigorion@yandex.ru}")
    private String smtpReplyTo;

    @Value("${keycloak.smtp.username:baryshevgrigorion@yandex.ru}")
    private String smtpUsername;

    @Value("${keycloak.smtp.password}")
    private String smtpPassword;

    @Value("${keycloak.smtp.ssl:true}")
    private boolean smtpSsl;

    @Value("${keycloak.smtp.starttls:false}")
    private boolean smtpStarttls;

    @Value("${keycloak.smtp.auth:true}")
    private boolean smtpAuth;

    @Value("${keycloak.init.enabled:true}")
    private boolean initEnabled;

    @PostConstruct
    public void initializeRealm() {
        if (!initEnabled) {
            log.info("Keycloak initialization is disabled");
            return;
        }

        try {
            log.info("Starting Keycloak initialization for realm: {}", this.realm);

            // Проверяем существование realm
            List<RealmRepresentation> realms = this.keycloakAdmin.realms().findAll();
            boolean realmExists = realms.stream().anyMatch(r -> r.getRealm().equals(this.realm));

            if (!realmExists) {
                log.info("Realm '{}' does not exist, creating...", this.realm);
                this.createRealm();
                this.createRoles();
                this.createClient();
                this.setupEmailVerification();
                this.setupSMTP();
                log.info("Keycloak realm '{}' initialized successfully", this.realm);
            } else {
                log.info("Realm '{}' already exists, updating configuration...", this.realm);
                // Обновляем существующий realm
                this.updateRealmSettings();
                this.setupSMTP();
                log.info("Realm '{}' updated successfully", this.realm);
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
        newRealm.setLoginWithEmailAllowed(true);
        newRealm.setDuplicateEmailsAllowed(false);
        newRealm.setResetPasswordAllowed(true);
        newRealm.setEditUsernameAllowed(false);
        newRealm.setSslRequired("EXTERNAL");
        newRealm.setAccessTokenLifespan(300);
        newRealm.setRefreshTokenMaxReuse(1800);
        newRealm.setAccessCodeLifespan(60);
        newRealm.setAccessCodeLifespanUserAction(300);
        newRealm.setAccessCodeLifespanLogin(1800);

        // Включаем верификацию email на уровне realm
        newRealm.setVerifyEmail(true);
        newRealm.setLoginWithEmailAllowed(true);

        this.keycloakAdmin.realms().create(newRealm);
        log.info("Realm '{}' created", this.realm);
    }

    private void createRoles() {
        log.info("Creating roles for realm: {}", this.realm);

        List<RoleRepresentation> roles = Arrays.asList(
                createRole("ROLE_USER", "Regular user role"),
                createRole("ROLE_ADMIN", "Administrator role"),
                createRole("ROLE_MODERATOR", "Moderator role")
        );

        for (RoleRepresentation role : roles) {
            try {
                this.keycloakAdmin.realm(this.realm).roles().create(role);
                log.info("Role '{}' created", role.getName());
            } catch (Exception e) {
                log.warn("Role '{}' already exists or error: {}", role.getName(), e.getMessage());
            }
        }
    }

    private RoleRepresentation createRole(String name, String description) {
        RoleRepresentation role = new RoleRepresentation();
        role.setName(name);
        role.setDescription(description);
        return role;
    }

    private void createClient() {
        log.info("Creating client: {}", this.clientId);

        ClientRepresentation client = new ClientRepresentation();
        client.setClientId(this.clientId);
        client.setName("BookStatistic Client");
        client.setDescription("BookStatistic application client");
        client.setEnabled(true);
        client.setPublicClient(false);
        client.setSecret(this.clientSecret);
        client.setStandardFlowEnabled(true);
        client.setDirectAccessGrantsEnabled(true);
        client.setServiceAccountsEnabled(true);
        client.setImplicitFlowEnabled(false);

        // Настройки для верификации email
        client.setRedirectUris(List.of(
                "http://localhost:8081/*",
                "http://localhost:8082/*",
                "http://localhost:8083/*",
                "http://localhost:8084/*",
                "http://localhost:8085/*",
                "http://localhost:8080/*"
        ));

        client.setWebOrigins(List.of(
                "http://localhost:8081",
                "http://localhost:8082",
                "http://localhost:8083",
                "http://localhost:8084",
                "http://localhost:8085",
                "http://localhost:8080"
        ));

        // Настройки для Required Actions
        Map<String, String> attributes = new HashMap<>();
        attributes.put("login_theme", "keycloak");
        attributes.put("access.token.lifespan", "300");
        client.setAttributes(attributes);

        try {
            this.keycloakAdmin.realm(this.realm).clients().create(client);
            log.info("Client '{}' created", this.clientId);
        } catch (Exception e) {
            log.warn("Client '{}' already exists or error: {}", this.clientId, e.getMessage());
        }
    }

    private void setupEmailVerification() {
        log.info("Setting up email verification for realm: {}", this.realm);

        try {
            // Получаем текущий realm
            RealmRepresentation realmRep = this.keycloakAdmin.realm(this.realm).toRepresentation();

            // Включаем верификацию email
            realmRep.setVerifyEmail(true);
            realmRep.setLoginWithEmailAllowed(true);
            realmRep.setDuplicateEmailsAllowed(false);

            // Обновляем realm
            this.keycloakAdmin.realm(this.realm).update(realmRep);
            log.info("Email verification enabled for realm: {}", this.realm);

        } catch (Exception e) {
            log.error("Failed to setup email verification: {}", e.getMessage());
        }
    }

    private void setupSMTP() {
        log.info("Setting up SMTP for realm: {}", this.realm);

        try {
            // Получаем текущий realm
            RealmRepresentation realmRep = this.keycloakAdmin.realm(this.realm).toRepresentation();

            // Настраиваем SMTP
            Map<String, String> smtpConfig = new HashMap<>();
            smtpConfig.put("host", this.smtpHost);
            smtpConfig.put("port", this.smtpPort);
            smtpConfig.put("from", this.smtpFrom);
            smtpConfig.put("fromDisplayName", this.smtpFromDisplayName);
            smtpConfig.put("replyTo", this.smtpReplyTo);
            smtpConfig.put("user", this.smtpUsername);
            smtpConfig.put("password", this.smtpPassword);
            smtpConfig.put("auth", String.valueOf(this.smtpAuth));
            smtpConfig.put("ssl", String.valueOf(this.smtpSsl));
            smtpConfig.put("starttls", String.valueOf(this.smtpStarttls));

            if (this.smtpHost.contains("yandex")) {
                smtpConfig.put("smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                smtpConfig.put("smtp.socketFactory.port", this.smtpPort);
            }

            realmRep.setSmtpServer(smtpConfig);

            this.keycloakAdmin.realm(this.realm).update(realmRep);
            log.info("SMTP configuration updated for realm: {}", this.realm);
            log.info("SMTP Host: {}, From: {}", this.smtpHost, this.smtpFrom);

        } catch (Exception e) {
            log.error("Failed to setup SMTP: {}", e.getMessage());
        }
    }

    private void updateRealmSettings() {
        log.info("Updating realm settings: {}", this.realm);

        try {
            RealmRepresentation realmRep = this.keycloakAdmin.realm(this.realm).toRepresentation();

            // Обновляем настройки
            realmRep.setVerifyEmail(true);
            realmRep.setLoginWithEmailAllowed(true);
            realmRep.setDuplicateEmailsAllowed(false);
            realmRep.setResetPasswordAllowed(true);

            this.keycloakAdmin.realm(this.realm).update(realmRep);
            log.info("Realm settings updated");

        } catch (Exception e) {
            log.error("Failed to update realm settings: {}", e.getMessage());
        }
    }

    /**
     * Метод для проверки SMTP конфигурации
     */
    public boolean testSmtpConnection() {
        try {
            RealmRepresentation realmRep = this.keycloakAdmin.realm(this.realm).toRepresentation();
            Map<String, String> smtp = realmRep.getSmtpServer();
            return smtp != null && !smtp.isEmpty() && smtp.containsKey("host");
        } catch (Exception e) {
            log.error("SMTP test failed: {}", e.getMessage());
            return false;
        }
    }
}