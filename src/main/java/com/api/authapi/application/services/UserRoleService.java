package com.api.authapi.application.services;

import com.api.authapi.application.exceptions.InvalidRoleException;
import com.api.authapi.application.exceptions.RoleAlreadyAssignedException;
import com.api.authapi.domain.dto.role.RoleResponse;
import com.api.authapi.domain.model.Role;
import com.api.authapi.domain.model.User;
import com.api.authapi.domain.model.UserRole;
import com.api.authapi.infrastructure.persistence.repositories.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;
    private final RoleService roleService;

    @Transactional
    public void assignRoleToUser(User user, String roleName) {
        log.info("[UserRoleService::assignRoleToUser] Assigning role '{}' to userId={}", roleName, user.getId());
        Role role = roleService.getRoleByName(roleName);
        if (!roleService.getAllRoles().stream().map(RoleResponse::getName).toList().contains(role.getName())) {
            log.warn("[UserRoleService::assignRoleToUser] Role '{}' is not found", roleName);
            throw new InvalidRoleException();
        }
        if (userRoleRepository.existsByUserAndRole(user, role)) {
            log.warn("[UserRoleService::assignRoleToUser] Role '{}' already assigned to userId={}", roleName, user.getId());
            throw new RoleAlreadyAssignedException();
        }
        UserRole userRole = UserRole.builder()
                .user(user)
                .role(role)
                .build();
        user.getRoles().add(userRole);
        userRoleRepository.save(userRole);
        log.info("[UserRoleService::assignRoleToUser] Role '{}' successfully assigned to userId={}", roleName, user.getId());
    }
}
