package com.api.authapi.application.services.auth;

import com.api.authapi.domain.dto.auth.AuthResponse;
import com.api.authapi.domain.dto.auth.LoginRequest;
import com.api.authapi.domain.dto.auth.LogoutRequest;
import com.api.authapi.domain.dto.auth.RefreshRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final LoginService loginService;
    private final CloseAllSessionsService closeAllSessionsService;
    private final LogoutService logoutService;
    private final RefreshSessionService refreshSessionService;

    @Transactional
    public AuthResponse login(LoginRequest request) {
        return loginService.execute(request);
    }

    @Transactional
    public void logout(LogoutRequest request) {
        logoutService.execute(request);
    }

    @Transactional
    public AuthResponse refresh(RefreshRequest request) {
        return refreshSessionService.execute(request);
    }

    @Transactional
    public void closeAllSessions() {
        closeAllSessionsService.execute();
    }
}
