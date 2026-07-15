package org.jedi_bachelor.bookstatistic.gateway.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {
    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    @Value("${bookstatistic.routing.keycloak}")
    private String keycloakRequestPath;

    @Value("${bookstatistic.routing.notification}")
    private String notificationRequestPath;

    @Value("${bookstatistic.routing.book}")
    private String bookRequestPath;

    @Value("${bookstatistic.routing.analyze}")
    private String analyzeRequestPath;

    @Value("${bookstatistic.routing.gateway}")
    private String gatewayRequestPath;

    @Value("${bookstatistic.routing.account}")
    private String accountRequestPath;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 1. Публичные эндпоинты (без авторизации)
                .authorizeExchange(exchange -> exchange
                        // Регистрация и health checks
                        .pathMatchers(
                                "/v1/auth/register",
                                "/v1/auth/login"
                        ).permitAll()

                        // Swagger / OpenAPI
                        .pathMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                )

                // 2. Эндпоинты с проверкой ролей
                .authorizeExchange(exchange -> exchange
                        // Только для USER, ADMIN, MODERATOR
                        .pathMatchers("/v1/books/**").hasAnyRole("USER", "ADMIN", "MODERATOR")
                        .pathMatchers("/v1/analyze/**").hasAnyRole("USER", "ADMIN", "MODERATOR")
                        .pathMatchers("/v1/notifications/**").hasAnyRole("USER", "ADMIN", "MODERATOR")

                        // Только для ADMIN и MODERATOR
                        .pathMatchers("/v1/users/**").hasAnyRole("ADMIN", "MODERATOR")
                        .pathMatchers("/v1/books/admin/**").hasAnyRole("ADMIN", "MODERATOR")

                        // Только для ADMIN
                        .pathMatchers("/v1/analyze/training/**",
                                "/actuator/health",
                                "/actuator/info",
                                "/actuator/prometheus").hasRole("ADMIN")

                        // Все остальные запросы требуют аутентификации
                        .anyExchange().authenticated()
                )

                // 3. Настройка JWT Resource Server
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtDecoder(reactiveJwtDecoder())
                                .jwtAuthenticationConverter(grantedAuthoritiesExtractor())
                        )
                        .authenticationEntryPoint((exchange, e) -> {
                            // Кастомный ответ при 401
                            exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
                            return exchange.getResponse().writeWith(
                                    Mono.just(exchange.getResponse()
                                            .bufferFactory()
                                            .wrap("{\"error\":\"Unauthorized\",\"message\":\"Invalid or missing token\"}".getBytes())
                                    )
                            );
                        })
                )

                // 4. Access Denied Handler
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler((exchange, denied) -> {
                            exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.FORBIDDEN);
                            return exchange.getResponse().writeWith(
                                    Mono.just(exchange.getResponse()
                                            .bufferFactory()
                                            .wrap("{\"error\":\"Forbidden\",\"message\":\"Insufficient permissions\"}".getBytes())
                                    )
                            );
                        })
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                this.keycloakRequestPath,  // Keycloak
                this.notificationRequestPath,  // Notification service
                this.bookRequestPath,  // Book service
                this.analyzeRequestPath,  // Analyze service
                this.gatewayRequestPath,  // Gateway
                this.accountRequestPath   // Account service
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder() {
        return NimbusReactiveJwtDecoder.withJwkSetUri(this.jwkSetUri).build();
    }

    @Bean
    public Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakGrantedAuthoritiesConverter());
        return new ReactiveJwtAuthenticationConverterAdapter(converter);
    }

    static class KeycloakGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
        @Override
        public Collection<GrantedAuthority> convert(Jwt jwt) {
            Collection<GrantedAuthority> authorities = new java.util.ArrayList<>();

            // Извлечение роли из realm_access
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess != null && realmAccess.containsKey("roles")) {
                List<String> roles = (List<String>) realmAccess.get("roles");
                authorities.addAll(roles.stream()
                        .filter(role -> role.startsWith("ROLE_") || role.equals("ROLE_USER"))
                        .map(role -> new SimpleGrantedAuthority(role.startsWith("ROLE_") ? role : "ROLE_" + role))
                        .collect(Collectors.toList()));
            }

            // Извлечение роли из resource_access (клиентские роли)
            Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
            if (resourceAccess != null) {
                Map<String, Object> clientAccess = (Map<String, Object>) resourceAccess.get("bookstatistic");
                if (clientAccess != null && clientAccess.containsKey("roles")) {
                    List<String> roles = (List<String>) clientAccess.get("roles");
                    authorities.addAll(roles.stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                            .collect(Collectors.toList()));
                }
            }

            return authorities;
        }
    }
}