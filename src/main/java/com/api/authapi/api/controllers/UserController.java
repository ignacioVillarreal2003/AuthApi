package com.api.authapi.api.controllers;

import com.api.authapi.application.services.user.UserService;
import com.api.authapi.domain.dto.user.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController()
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PutMapping("/me/password")
    public ResponseEntity<UserResponse> updatePassword(@Valid @RequestBody UpdatePasswordRequest request) {
        userService.changePassword(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/me/reactivation")
    public ResponseEntity<UserResponse> reactiveAccount(@Valid @RequestBody ReactivationRequest request) {
        userService.requestAccountReactivation(request);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/me/enable/{activationToken}")
    public ResponseEntity<Void> enableAccount(@PathVariable UUID activationToken) {
        userService.activateAccount(activationToken);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/me/disable")
    public ResponseEntity<Void> disableAccount() {
        userService.deactivateAccount();
        return ResponseEntity.noContent().build();
    }
}
