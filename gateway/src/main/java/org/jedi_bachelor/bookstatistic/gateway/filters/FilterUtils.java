package org.jedi_bachelor.bookstatistic.gateway.filters;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;
import java.util.UUID;

@Component
public class FilterUtils {
    public static final String CORRELATION_ID = "tmx-correlation-id";
    public static final String AUTH_TOKEN     = "tmx-auth-token";
    public static final String USER_ID        = "tmx-user-id";
    public static final String ORG_ID         = "tmx-org-id";
    public static final String PRE_FILTER_TYPE = "pre";
    public static final String POST_FILTER_TYPE = "post";
    public static final String ROUTE_FILTER_TYPE = "route";
    public static final String ACCEPT_LANGUAGE = "Accept-Language";

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

    private ServerWebExchange setRequestHeader(ServerWebExchange exchange, String name, String value) {
        return exchange.mutate().request(
                        exchange.getRequest().mutate()
                                .header(name, value)
                                .build()
                ).build();
    }
}
