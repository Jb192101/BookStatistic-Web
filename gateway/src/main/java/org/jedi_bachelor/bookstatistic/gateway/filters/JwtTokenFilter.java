package org.jedi_bachelor.bookstatistic.gateway.filters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.gateway.utils.FilterUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collection;

@Order(0)
@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenFilter implements GlobalFilter {
    private final FilterUtils filterUtils;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
                .map(context -> context.getAuthentication())
                .flatMap(authentication -> {
                    ServerWebExchange mutatedExchange = exchange;

                    if (authentication != null && authentication.isAuthenticated()) {
                        String token = extractToken(exchange);

                        // Извлекаем user ID из токена
                        String userId = extractUserId(authentication);
                        if (userId != null) {
                            mutatedExchange = filterUtils.setUserId(mutatedExchange, userId);
                            log.debug("User ID: {}", userId);
                        }

                        // Извлекаем роль
                        String role = extractRole(authentication);
                        if (role != null) {
                            mutatedExchange = filterUtils.setRole(mutatedExchange, role);
                            log.debug("Role: {}", role);
                        }

                        // Добавляем токен
                        if (token != null) {
                            mutatedExchange = filterUtils.setAuthToken(mutatedExchange, token);
                        }
                    }

                    return chain.filter(mutatedExchange);
                })
                .switchIfEmpty(chain.filter(exchange));
    }

    private String extractToken(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    private String extractUserId(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof Jwt) {
            Jwt jwt = (Jwt) principal;
            return jwt.getClaimAsString("sub");
        }
        return authentication.getName();
    }

    private String extractRole(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (authorities != null && !authorities.isEmpty()) {
            return authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .filter(role -> role.startsWith("ROLE_"))
                    .findFirst()
                    .orElse("ROLE_USER")
                    .replace("ROLE_", "");
        }
        return "USER";
    }
}
