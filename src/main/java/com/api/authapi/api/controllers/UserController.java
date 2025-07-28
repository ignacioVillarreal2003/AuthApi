package com.api.authapi.api.controllers;

import com.api.authapi.application.services.user.UserService;
import com.api.authapi.domain.dto.user.VerifyAccountRequest;
import com.api.authapi.domain.dto.user.ReactiveAccountRequest;
import com.api.authapi.domain.dto.user.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping("/account/verify")
    public ResponseEntity<UserResponse> verifyAccount(@Valid @RequestBody VerifyAccountRequest request) {
        userService.verifyAccount(request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/account/password")
    public ResponseEntity<UserResponse> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/account/reactivation-request")
    public ResponseEntity<UserResponse> requestAccountReactivation(@Valid @RequestBody RequestAccountReactivationRequest request) {
        userService.requestAccountReactivation(request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/account/reactivate")
    public ResponseEntity<Void> reactivateAccount(@Valid @RequestBody ReactiveAccountRequest request) {
        userService.reactivateAccount(request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/account/deactivate")
    public ResponseEntity<Void> deactivateAccount() {
        userService.deactivateAccount();
        return ResponseEntity.noContent().build();
    }
}
