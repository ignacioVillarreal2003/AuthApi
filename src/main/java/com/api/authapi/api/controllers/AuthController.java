package com.api.authapi.api.controllers;

import com.api.authapi.application.services.AuthService;
import com.api.authapi.domain.dto.auth.AuthResponse;
import com.api.authapi.domain.dto.auth.LoginUserRequest;
import com.api.authapi.domain.dto.auth.RefreshTokenRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginUserRequest request) {
        log.info("[AuthController::login] Login attempt for email={}", request.email());
        AuthResponse response = authService.login(request);
        log.info("[AuthController::login] Login successful for email={}", request.email());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        log.info("[AuthController::logout] Logout requested");
        authService.logout();
        log.info("[AuthController::logout] Logout completed");
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest refreshToken) {
        log.info("[AuthController::refresh] Refresh token request received");
        AuthResponse response = authService.refresh(refreshToken);
        log.info("[AuthController::refresh] Refresh token issued for userId={}", response.getUser().getId());
        return ResponseEntity.ok(response);
    }
}
