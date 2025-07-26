package com.api.authapi.application.services.auth;

import com.api.authapi.application.exceptions.InvalidRefreshTokenException;
import com.api.authapi.application.helpers.UserHelperService;
import com.api.authapi.application.services.refreshToken.GetRefreshTokenByTokenService;
import com.api.authapi.application.services.refreshToken.RevokeRefreshTokenService;
import com.api.authapi.domain.dto.auth.AuthResponse;
import com.api.authapi.domain.dto.auth.RefreshRequest;
import com.api.authapi.domain.model.RefreshToken;
import com.api.authapi.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RefreshSessionService {

    private final UserHelperService userHelperService;
    private final BuildAuthService buildAuthService;
    private final GetRefreshTokenByTokenService getRefreshTokenByTokenService;
    private final RevokeRefreshTokenService revokeRefreshTokenService;

    public AuthResponse execute(RefreshRequest refreshRequest) {
        RefreshToken refreshToken = getRefreshTokenByTokenService.execute(refreshRequest.refreshToken());
        User user = refreshToken.getUser();
        userHelperService.verifyAccountStatus(user);
        if (refreshToken.isRevoked()
                || Instant.now().isAfter(refreshToken.getExpiresAt())) {
            throw new InvalidRefreshTokenException();
        }
        revokeRefreshTokenService.execute(refreshToken.getId());
        return buildAuthService.execute(user);
    }
}
