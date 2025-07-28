package com.api.authapi.api.controllers;

import com.api.authapi.application.services.authentication.AuthenticationService;
import com.api.authapi.domain.dto.auth.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("api/v1/authentication")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final AuthenticationService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse authResponse = authService.login(request);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest request) {
        authService.logout(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/sessions/refresh")
    public ResponseEntity<AuthResponse> refreshSession(@Valid @RequestBody RefreshSessionRequest request) {
        AuthResponse authResponse = authService.refreshSession(request);
        return ResponseEntity.ok(authResponse);
    }

    @DeleteMapping("/sessions/logout-all")
    public ResponseEntity<Void> logoutAllSessions() {
        authService.logoutAllSessions();
        return ResponseEntity.noContent().build();
    }
}
