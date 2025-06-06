package com.api.authapi.api.controllers;

import com.api.authapi.application.services.UserRoleService;
import com.api.authapi.domain.dtos.userRole.UserRoleResponse;
import com.api.authapi.domain.enums.Role;
import com.api.authapi.domain.models.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("api/v1/user-roles")
@RequiredArgsConstructor
public class UserRoleController {

    private final UserRoleService userRoleService;

    @PostMapping("/users/{userId}/roles/{roleName}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<UserRoleResponse> addUserRole(@PathVariable Long userId,
                                                        @PathVariable Role roleName) {
        UserRoleResponse response = userRoleService.createUserRole(userId, roleName);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/users/{userId}/roles/{roleName}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> removeUserRole(@PathVariable Long userId,
                                               @PathVariable Role roleName) {
        userRoleService.deleteUserRoleByUserAndRole(userId, roleName);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/users/{userId}/roles")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> removeAllUserRoles(@PathVariable Long userId) {
        userRoleService.deleteAllUserRoleByUser(userId);
        return ResponseEntity.noContent().build();
    }
}
