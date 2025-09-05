package dev.ignacio.villarreal.authenticationapi.application.services.authentication;

import dev.ignacio.villarreal.authenticationapi.application.helpers.UserHelperService;
import dev.ignacio.villarreal.authenticationapi.application.services.refreshToken.RefreshTokenRetrievalService;
import dev.ignacio.villarreal.authenticationapi.application.services.refreshToken.RefreshTokenRevocationService;
import dev.ignacio.villarreal.authenticationapi.domain.model.RefreshToken;
import dev.ignacio.villarreal.authenticationapi.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogoutService {

    private final UserHelperService userHelperService;
    private final RefreshTokenRetrievalService refreshTokenRetrievalService;
    private final RefreshTokenRevocationService refreshTokenRevocationService;

    public void logout(String token) {
        log.info("Logout attempt with provided refresh token");

        RefreshToken refreshToken = refreshTokenRetrievalService.getByToken(token);
        refreshTokenRevocationService.revokeById(refreshToken.getId());

        log.info("Session revoked for user: {}", refreshToken.getUser().getEmail());
    }

    public void logoutAllSessions() {
        log.info("Revoking all sessions");

        User user = userHelperService.getCurrentUser();

        refreshTokenRevocationService.revokeAllByUserId(user.getId());

        log.info("All sessions successfully revoked for user: {}", user.getEmail());
    }
}
