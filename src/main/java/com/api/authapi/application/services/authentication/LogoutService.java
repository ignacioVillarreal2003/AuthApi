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
        log.info("[LogoutService::logout] - Starting logout process");

        User user = userHelperService.getCurrentUser();
        userHelperService.verifyAccountStatus(user);

        RefreshToken refreshToken = refreshTokenRetrievalService.getByToken(token);
        refreshTokenRevocationService.revokeById(refreshToken.getId());

        log.info("[LogoutService::logout] - Session successfully revoked");
    }

    public void logoutAllSessions() {
        log.info("[LogoutAllSessionsService::logoutAllSessions] - Initiating logout from all sessions");

        User user = userHelperService.getCurrentUser();
        userHelperService.verifyAccountStatus(user);

        refreshTokenRevocationService.revokeAllByUserId(user.getId());

        log.info("[LogoutAllSessionsService::logoutAllSessions] - All user sessions successfully revoked");
    }
}
