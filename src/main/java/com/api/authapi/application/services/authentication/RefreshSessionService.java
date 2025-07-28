package com.api.authapi.application.services.authentication;

import com.api.authapi.application.exceptions.unauthorized.InvalidRefreshTokenException;
import com.api.authapi.application.helpers.UserHelperService;
import com.api.authapi.application.services.refreshToken.RefreshTokenRetrievalService;
import com.api.authapi.application.services.refreshToken.RefreshTokenRevocationService;
import com.api.authapi.domain.dto.auth.AuthResponse;
import com.api.authapi.domain.model.RefreshToken;
import com.api.authapi.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshSessionService {

    private final UserHelperService userHelperService;
    private final AuthResponseBuilderService authResponseBuilderService;
    private final RefreshTokenRetrievalService refreshTokenRetrievalService;
    private final RefreshTokenRevocationService refreshTokenRevocationService;

    public AuthResponse refreshSession(String token) {
        log.info("Attempting to refresh session with provided token");

        RefreshToken refreshToken = refreshTokenRetrievalService.getByToken(token);
        User user = refreshToken.getUser();

        log.debug("Refresh token belongs to user: {}", user.getEmail());

        userHelperService.verifyAccountStatus(user);

        if (refreshToken.isRevoked()) {
            log.warn("Token already revoked for user: {}", user.getEmail());
            throw new InvalidRefreshTokenException();
        }

        if (Instant.now().isAfter(refreshToken.getExpiresAt())) {
            log.warn("Token expired for user: {}", user.getEmail());
            throw new InvalidRefreshTokenException();
        }

        refreshTokenRevocationService.revokeById(refreshToken.getId());

        log.info("Session successfully refreshed for user: {}", user.getEmail());
        return authResponseBuilderService.build(user);
    }
}
