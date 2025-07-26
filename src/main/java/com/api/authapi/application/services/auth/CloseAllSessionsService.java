package com.api.authapi.application.services.auth;

import com.api.authapi.application.helpers.UserHelperService;
import com.api.authapi.application.services.refreshToken.CleanupRefreshTokenService;
import com.api.authapi.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CloseAllSessionsService {

    private final UserHelperService userHelperService;
    private final CleanupRefreshTokenService cleanupRefreshTokenService;

    public void execute() {
        User user = userHelperService.getCurrentUser();
        userHelperService.verifyAccountStatus(user);
        cleanupRefreshTokenService.execute(user.getId());
    }
}
