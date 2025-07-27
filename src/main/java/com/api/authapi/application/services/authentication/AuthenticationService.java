package com.api.authapi.application.services.authentication;

import com.api.authapi.domain.dto.auth.AuthResponse;
import com.api.authapi.domain.dto.auth.LoginRequest;
import com.api.authapi.domain.dto.auth.LogoutRequest;
import com.api.authapi.domain.dto.auth.RefreshRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final LoginService loginService;
    private final LogoutService logoutService;
    private final RefreshSessionService refreshSessionService;

    @Transactional
    public AuthResponse login(LoginRequest request) {
        return loginService.login(request.email(),
                request.password());
    }

    @Transactional
    public void logout(LogoutRequest request) {
        logoutService.logout(request.refreshToken());
    }

    @Transactional
    public AuthResponse refreshSession(RefreshRequest request) {
        return refreshSessionService.refreshSession(request.refreshToken());
    }

    @Transactional
    public void logoutAllSessions() {
        logoutService.logoutAllSessions();
    }
}
