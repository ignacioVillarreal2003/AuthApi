package com.api.authapi.api.controllers;

import com.api.authapi.application.saga.orchestrator.UserRegistrationOrchestrator;
import com.api.authapi.application.services.authentication.AuthenticationService;
import com.api.authapi.application.services.user.UserService;
import com.api.authapi.domain.dto.auth.AuthResponse;
import com.api.authapi.domain.dto.auth.LoginRequest;
import com.api.authapi.domain.dto.auth.LogoutRequest;
import com.api.authapi.domain.dto.auth.RefreshRequest;
import com.api.authapi.domain.dto.user.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController()
@RequestMapping("api/v1/authentication")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final AuthenticationService authService;
    private final UserService userService;
    private final UserRegistrationOrchestrator userRegistrationOrchestrator;

    @GetMapping("/activation/{activationToken}/{sagaId}")
    public ResponseEntity<UserResponse> activeAccount(@PathVariable UUID activationToken, @PathVariable UUID sagaId) {
        userService.activateAccount(activationToken);
        userRegistrationOrchestrator.handleActiveAccount(sagaId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse authResponse = authService.login(loginRequest);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest logoutRequest) {
        authService.logout(logoutRequest);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshSession(@Valid @RequestBody RefreshRequest refreshRequest) {
        AuthResponse authResponse = authService.refreshSession(refreshRequest);
        return ResponseEntity.ok(authResponse);
    }

    @DeleteMapping("/sessions")
    public ResponseEntity<Void> logoutAllSessions() {
        authService.logoutAllSessions();
        return ResponseEntity.noContent().build();
    }
}
