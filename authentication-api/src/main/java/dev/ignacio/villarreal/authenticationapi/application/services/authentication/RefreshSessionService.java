package dev.ignacio.villarreal.authenticationapi.application.services.authentication;

import dev.ignacio.villarreal.authenticationapi.application.exceptions.unauthorized.InvalidRefreshTokenException;
import dev.ignacio.villarreal.authenticationapi.application.helpers.UserHelperService;
import dev.ignacio.villarreal.authenticationapi.application.services.refreshToken.RefreshTokenRetrievalService;
import dev.ignacio.villarreal.authenticationapi.application.services.refreshToken.RefreshTokenRevocationService;
import dev.ignacio.villarreal.authenticationapi.domain.dto.auth.AuthResponse;
import dev.ignacio.villarreal.authenticationapi.domain.model.RefreshToken;
import dev.ignacio.villarreal.authenticationapi.domain.model.User;
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
