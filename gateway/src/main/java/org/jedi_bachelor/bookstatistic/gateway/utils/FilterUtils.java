package org.jedi_bachelor.bookstatistic.gateway.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Component
public class FilterUtils {
    public static final String CORRELATION_ID = "tmx-correlation-id";
    public static final String AUTH_TOKEN     = "tmx-auth-token";
    public static final String USER_ID        = "tmx-user-id";
    public static final String ROUTE_FILTER_TYPE = "route";
    public static final String ACCEPT_LANGUAGE = "Accept-Language";
    public static final String ROLE = "tmx-role";

    public String getCorrelationId(HttpHeaders requestHeaders) {
        if (requestHeaders.get(CORRELATION_ID) != null) {
            List<String> header = requestHeaders.get(CORRELATION_ID);
            return header.stream().findFirst().get();
        } else {
            return null;
        }
    }

    public ServerWebExchange setCorrelationId(ServerWebExchange exchange, String correlationId) {
        return this.setRequestHeader(exchange, CORRELATION_ID, correlationId);
    }

    public ServerWebExchange setUserId(ServerWebExchange exchange, UUID userId) {
        return this.setRequestHeader(exchange, USER_ID, userId.toString());
    }

    public ServerWebExchange setUserId(ServerWebExchange exchange, String userId) {
        return this.setRequestHeader(exchange, USER_ID, userId);
    }

    public String getUserId(HttpHeaders requestHeaders) {
        if (requestHeaders.get(USER_ID) != null) {
            List<String> header = requestHeaders.get(USER_ID);
            return header.stream().findFirst().get();
        } else {
            return null;
        }
    }

    public String getAcceptLanguage(HttpHeaders requestHeaders) {
        if(requestHeaders.get(ACCEPT_LANGUAGE) != null) {
            List<String> header = requestHeaders.get(ACCEPT_LANGUAGE);
            return header.stream().findFirst().get();
        } else {
            return "EN";
        }
    }

    public ServerWebExchange setAcceptLanguage(ServerWebExchange exchange, String language) {
        return this.setRequestHeader(exchange, ACCEPT_LANGUAGE, language);
    }

    /**
     * Метод извлечения токена Bearer из заголовка
     *
     * @param exchange
     * @return
     */
    private String extractToken(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    /**
     * Метод извлечения ID пользователя
     */
    private String extractUserId(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof org.springframework.security.oauth2.jwt.Jwt) {
            Jwt jwt = (Jwt) principal;
            return jwt.getClaimAsString("sub");
        }
        return authentication.getName();
    }

    /**
     * Метод извлечения роли из authentication
     */
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

    public ServerWebExchange setRole(ServerWebExchange exchange, String role) {
        return this.setRequestHeader(exchange, ROLE, role);
    }

    public ServerWebExchange setAuthToken(ServerWebExchange exchange, String token) {
        return this.setRequestHeader(exchange, AUTH_TOKEN, token);
    }

    /**
     * Общий метод для установки значения в HTTP заголовки
     *
     * @param name название хэдера
     * @param value значение хэдера
     * @return новый exchange
     */
    private ServerWebExchange setRequestHeader(ServerWebExchange exchange, String name, String value) {
        return exchange.mutate().request(
                        exchange.getRequest().mutate()
                                .header(name, value)
                                .build()
                ).build();
    }
}
