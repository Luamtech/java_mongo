package com.pakal.cloud.repository;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.DefaultCsrfToken;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

public class CustomCsrfTokenRepository implements CsrfTokenRepository {

    private final ConcurrentHashMap<String, ExpiringCsrfToken> tokenStorage = new ConcurrentHashMap<>();
    private static final long EXPIRATION_TIME_MILLIS = 1 * 60 * 1000; // 1 minuto

    @Override
    public CsrfToken generateToken(HttpServletRequest request) {
        String tokenValue = java.util.UUID.randomUUID().toString();
        String headerName = "X-XSRF-TOKEN";
        String parameterName = "_csrf";

        ExpiringCsrfToken csrfToken = new ExpiringCsrfToken(
                new DefaultCsrfToken(headerName, parameterName, tokenValue),
                Instant.now().plusMillis(EXPIRATION_TIME_MILLIS)
        );

        tokenStorage.put(tokenValue, csrfToken);
        return csrfToken.getCsrfToken();
    }

    @Override
    public void saveToken(CsrfToken token, HttpServletRequest request, HttpServletResponse response) {
        if (token == null || token.getToken() == null) {
            return;
        }
        tokenStorage.put(token.getToken(), new ExpiringCsrfToken(token, Instant.now().plusMillis(EXPIRATION_TIME_MILLIS)));
    }

    @Override
    public CsrfToken loadToken(HttpServletRequest request) {
        String tokenValue = request.getHeader("X-XSRF-TOKEN");

        // 🔹 Validar que el header existe antes de buscarlo en el mapa
        if (tokenValue == null || tokenValue.isEmpty()) {
            return null;
        }

        ExpiringCsrfToken csrfToken = tokenStorage.get(tokenValue);

        if (csrfToken == null || csrfToken.isExpired()) {
            return null;
        }

        return csrfToken.getCsrfToken();
    }

    private static class ExpiringCsrfToken {
        private final CsrfToken csrfToken;
        private final Instant expirationTime;

        ExpiringCsrfToken(CsrfToken csrfToken, Instant expirationTime) {
            this.csrfToken = csrfToken;
            this.expirationTime = expirationTime;
        }

        public CsrfToken getCsrfToken() {
            return csrfToken;
        }

        public boolean isExpired() {
            return Instant.now().isAfter(expirationTime);
        }
    }
}
