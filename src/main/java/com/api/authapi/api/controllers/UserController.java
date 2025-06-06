package com.api.authapi.api.controllers;

import com.api.authapi.application.services.UserService;
import com.api.authapi.domain.dtos.user.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/logout")
    public ResponseEntity<Void> logout() {
        userService.logout();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshRequest refreshToken) {
        AuthResponse response = userService.refresh(refreshToken);
        return ResponseEntity.ok(response);
    }

    @PutMapping()
    public ResponseEntity<UserResponse> updateUser(@Valid @RequestBody UpdateUserRequest userDto) {
        UserResponse response = userService.updateUser(userDto);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{userId}/enable")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> enableUser(@PathVariable Long userId) {
        userService.enableUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{userId}/disable")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> disableUser(@PathVariable Long userId) {
        userService.disableUser(userId);
        return ResponseEntity.noContent().build();
    }
}
