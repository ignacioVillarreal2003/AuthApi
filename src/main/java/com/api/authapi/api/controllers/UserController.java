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

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateUser(@Valid @RequestBody UpdateUserRequest userDto) {
        UserResponse response = userService.updateUser(userDto);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/enable")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> enableUser(@PathVariable("id") Long userId) {
        userService.enableUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/disable")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> disableUser(@PathVariable("id") Long userId) {
        userService.disableUser(userId);
        return ResponseEntity.noContent().build();
    }
}
