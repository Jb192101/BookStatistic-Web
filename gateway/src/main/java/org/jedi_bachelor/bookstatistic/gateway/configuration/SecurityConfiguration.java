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
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {
    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(
                                "/actuator/health",
                                "/actuator/info",
                                "/actuator/prometheus",
                                "/v1/auth/register"
                        ).permitAll()

                        .pathMatchers(
                                "/v1/books/**",
                                "/v1/analyze/**"
                        ).hasRole("USER")

                        .pathMatchers(
                                "GET",
                                "/v1/authors",
                                "/v1/authors/{authorId}"
                        ).hasAnyRole("USER", "ADMIN", "MODERATOR")

                        .pathMatchers(
                                "GET",
                                "/v1/books",
                                "/v1/books/{userId}/statistics",
                                "/v1/books/{bookId}/text",
                                "/v1/books/search",
                                "/v1/books/book-relations/{bookId}",
                                "/v1/books/user/{userId}"
                        ).hasAnyRole("USER", "ADMIN", "MODERATOR")

                        .pathMatchers(
                                "POST",
                                "/v1/responses"
                        ).hasAnyRole("USER", "ADMIN", "MODERATOR")

                        .pathMatchers(
                                "GET",
                                "/v1/responses/books/{bookId}",
                                "/v1/responses/books/{bookId}/all",
                                "/v1/responses/users/{userId}"
                        ).hasAnyRole("USER", "ADMIN", "MODERATOR")

                        .pathMatchers(
                                "PUT",
                                "/v1/responses"
                        ).hasAnyRole("USER", "ADMIN", "MODERATOR")

                        .pathMatchers(
                                "DELETE",
                                "/v1/responses/books/{bookId}"
                        ).hasAnyRole("USER", "ADMIN", "MODERATOR")

                        .pathMatchers(
                                "POST",
                                "/v1/authors",
                                "/v1/authors/book-relations"
                        ).hasAnyRole("ADMIN", "MODERATOR")

                        .pathMatchers(
                                "DELETE",
                                "/v1/authors/{authorId}",
                                "/v1/authors/book-relations"
                        ).hasAnyRole("ADMIN", "MODERATOR")

                        .pathMatchers(
                                "PUT",
                                "/v1/authors/{authorId}"
                        ).hasAnyRole("ADMIN", "MODERATOR")

                        .pathMatchers(
                                "GET",
                                "/v1/authors/book-relations",
                                "/v1/authors/book-relations/{authorId}"
                        ).hasAnyRole("ADMIN", "MODERATOR")

                        .pathMatchers(
                                "POST",
                                "/v1/books",
                                "/v1/books/reading"
                        ).hasAnyRole("ADMIN", "MODERATOR")

                        .pathMatchers(
                                "GET",
                                "/v1/books/{bookId}",
                                "/v1/books/texts",
                                "/v1/books/reading/{userId}",
                                "/v1/books/reading"
                        ).hasAnyRole("ADMIN", "MODERATOR")

                        .pathMatchers(
                                "PUT",
                                "/v1/books/{bookId}"
                        ).hasAnyRole("ADMIN", "MODERATOR")

                        .pathMatchers(
                                "PATCH",
                                "/v1/books/{bookId}",
                                "/v1/books/{bookId}/text",
                                "/v1/books/reading"
                        ).hasAnyRole("ADMIN", "MODERATOR")
                        .pathMatchers(
                                "DELETE",
                                "/v1/books/{bookId}",
                                "/v1/books/reading/{userId}"
                        ).hasAnyRole("ADMIN", "MODERATOR")

                        .pathMatchers(
                                "GET",
                                "/v1/books/outbox"
                        ).hasAnyRole("ADMIN", "MODERATOR")

                        .pathMatchers(
                                "GET",
                                "/v1/responses"
                        ).hasAnyRole("ADMIN", "MODERATOR")

                        .pathMatchers(
                                "GET",
                                "/v1/users"
                        ).hasAnyRole("ADMIN", "MODERATOR")
                        .pathMatchers(
                                "DELETE",
                                "/v1/users/{id}"
                        ).hasAnyRole("ADMIN", "MODERATOR")

                        // ADMIN
                        .pathMatchers(
                                "/v1/analyze/training/**"
                        ).hasRole("ADMIN")

                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtDecoder(reactiveJwtDecoder())
                                .jwtAuthenticationConverter(grantedAuthoritiesExtractor())
                        )
                );

        return http.build();
    }

    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder() {
        return NimbusReactiveJwtDecoder.withJwkSetUri(jwkSetUri).build();
    }

    @Bean
    public Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakGrantedAuthoritiesConverter());
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }

    static class KeycloakGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
        private final JwtGrantedAuthoritiesConverter defaultGrantedAuthoritiesConverter =
                new JwtGrantedAuthoritiesConverter();

        @Override
        public Collection<GrantedAuthority> convert(Jwt jwt) {
            Collection<GrantedAuthority> authorities = defaultGrantedAuthoritiesConverter.convert(jwt);

            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess != null && realmAccess.containsKey("roles")) {
                List<String> roles = (List<String>) realmAccess.get("roles");
                authorities = Stream.concat(
                                authorities.stream(),
                                roles.stream()
                                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        )
                        .collect(Collectors.toList());
            }

            Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
            if (resourceAccess != null) {
                Map<String, Object> clientAccess = (Map<String, Object>) resourceAccess.get("bookstatistic");
                if (clientAccess != null && clientAccess.containsKey("roles")) {
                    List<String> roles = (List<String>) clientAccess.get("roles");
                    authorities = Stream.concat(
                                    authorities.stream(),
                                    roles.stream()
                                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                            )
                            .collect(Collectors.toList());
                }
            }

            return authorities;
        }
    }
}