package org.jedi_bachelor.bookstatistic.notificationservice.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.UUID;

public class SecurityUtils {
    private static final String USER_ID_CLAIM = "sub";

    /**
     * Метод получения нынешнего userId пользователя, который производит запрос
     *
     * @return ID пользователя
     */
    public static UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            String userIdStr = jwt.getClaim(USER_ID_CLAIM);
            if (userIdStr == null) {
                userIdStr = jwt.getSubject();
            }
            return UUID.fromString(userIdStr);
        }

        throw new IllegalStateException("User not authenticated");
    }

    /**
     * Метод получения нынешнего имени пользователя из запроса
     *
     * @return имя
     */
    public static String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    /**
     * Метод проверки наличия конкретной роли в запросе
     *
     * @param role роль
     * @return true, если есть
     */
    public static boolean hasRole(String role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(role));
    }
}
