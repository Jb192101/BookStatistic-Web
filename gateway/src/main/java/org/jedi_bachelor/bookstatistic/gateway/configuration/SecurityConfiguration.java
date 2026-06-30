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
                                "/v1/auth/register",
                                "/v1/auth/login"
                        ).permitAll()
                        .pathMatchers(
                                "/v1/books/**",
                                "/v1/auth/**",
                                "/v1/analyze/**"
                        ).hasRole("USER")
                        .pathMatchers(
                                "/v1/users/**",
                                "/v1/notifications/**",
                                "/v1/email/**",
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