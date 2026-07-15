package org.jedi_bachelor.bookstatistic.gateway.filters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Order(1)
@Component
@Slf4j
@RequiredArgsConstructor
public class UserContextFilter implements GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
                .map(context -> context.getAuthentication())
                .flatMap(authentication -> {
                    ServerWebExchange mutatedExchange = exchange;

                    if (authentication != null && authentication.isAuthenticated()) {
                        Object principal = authentication.getPrincipal();
                        if (principal instanceof Jwt) {
                            Jwt jwt = (Jwt) principal;

                            // Извлечение user-id
                            String userId = jwt.getClaimAsString("sub");
                            if (userId != null) {
                                mutatedExchange = exchange.mutate()
                                        .request(r -> r.header("tmx-user-id", userId))
                                        .build();
                                log.debug("Added user-id header: {}", userId);
                            }

                            // Извлечение роли
                            String role = extractRole(authentication);
                            if (role != null) {
                                mutatedExchange = exchange.mutate()
                                        .request(r -> r.header("tmx-role", role))
                                        .build();
                                log.debug("Added role header: {}", role);
                            }

                            // Проброс токен (если нужно сервисам для вызовов Keycloak)
                            String token = jwt.getTokenValue();
                            if (token != null) {
                                mutatedExchange = exchange.mutate()
                                        .request(r -> r.header("tmx-auth-token", token))
                                        .build();
                            }
                        }
                    }

                    return chain.filter(mutatedExchange);
                })
                .switchIfEmpty(chain.filter(exchange));
    }

    private String extractRole(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .filter(role -> role.startsWith("ROLE_"))
                .findFirst()
                .map(role -> role.replace("ROLE_", ""))
                .orElse("USER");
    }
}