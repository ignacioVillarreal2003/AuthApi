package com.api.authapi.application.services.auth;

import com.api.authapi.application.helpers.UserHelperService;
import com.api.authapi.application.services.refreshToken.GetRefreshTokenByTokenService;
import com.api.authapi.application.services.refreshToken.RevokeRefreshTokenService;
import com.api.authapi.domain.dto.auth.LogoutRequest;
import com.api.authapi.domain.model.RefreshToken;
import com.api.authapi.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService {

    private final UserHelperService userHelperService;
    private final GetRefreshTokenByTokenService getRefreshTokenByTokenService;
    private final RevokeRefreshTokenService revokeRefreshTokenService;

    public void execute(LogoutRequest logoutRequest) {
        User user = userHelperService.getCurrentUser();
        userHelperService.verifyAccountStatus(user);
        RefreshToken refreshToken = getRefreshTokenByTokenService.execute(logoutRequest.refreshToken());
        revokeRefreshTokenService.execute(refreshToken.getId());
    }
}
