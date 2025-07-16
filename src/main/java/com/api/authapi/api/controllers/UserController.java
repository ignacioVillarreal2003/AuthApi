package com.api.authapi.api.controllers;

import com.api.authapi.application.services.UserService;
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

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateUser(@Valid @RequestBody UpdateUserRequest request) {
        log.info("[UserController::updateUser] Update user request received. Payload: {}", request);
        UserResponse response = userService.updateCurrentUser(request);
        log.info("[UserController::updateUser] User updated successfully. userId={}", response.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteUser() {
        log.info("[UserController::deleteUser] Delete current user request received");
        userService.deleteCurrentUser();
        log.info("[UserController::deleteUser] User deleted successfully");
        return ResponseEntity.noContent().build();
    }
}
