package com.api.authapi.api.controllers;

import com.api.authapi.application.services.UserRoleService;
import com.api.authapi.domain.dtos.userRole.UserRoleResponse;
import com.api.authapi.domain.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("api/v1/users/{id}/roles")
@RequiredArgsConstructor
public class UserRoleController {

    private final UserRoleService userRoleService;

    @PostMapping("/{role}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<UserRoleResponse> assignRole(@PathVariable("id") Long userId,
                                                       @PathVariable Role role) {
        UserRoleResponse response = userRoleService.assignRole(userId, role);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{role}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> removeRole(@PathVariable("id") Long userId,
                                           @PathVariable Role role) {
        userRoleService.removeRole(userId, role);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping()
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> removeAllRoles(@PathVariable("id") Long userId) {
        userRoleService.removeAllRoles(userId);
        return ResponseEntity.noContent().build();
    }
}
