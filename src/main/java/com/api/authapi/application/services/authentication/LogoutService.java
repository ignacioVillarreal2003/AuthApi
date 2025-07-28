package com.api.authapi.application.services.authentication;

import com.api.authapi.application.helpers.UserHelperService;
import com.api.authapi.application.services.refreshToken.RefreshTokenRetrievalService;
import com.api.authapi.application.services.refreshToken.RefreshTokenRevocationService;
import com.api.authapi.domain.model.RefreshToken;
import com.api.authapi.domain.model.User;
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
