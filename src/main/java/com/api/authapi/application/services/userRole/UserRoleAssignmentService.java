package com.api.authapi.application.services.userRole;

import com.api.authapi.application.exceptions.RoleAlreadyAssignedException;
import com.api.authapi.application.services.role.RoleRetrievalService;
import com.api.authapi.domain.model.Role;
import com.api.authapi.domain.model.User;
import com.api.authapi.domain.model.UserRole;
import com.api.authapi.infrastructure.persistence.repositories.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserRoleAssignmentService {

    private final UserRoleRepository userRoleRepository;
    private final RoleRetrievalService roleRetrievalService;

    public void assignRoleToUser(User user, String roleName) {
        log.debug("[UserRoleAssignmentService::assignRoleToUser] - Assigning role '{}' to user ID {}", roleName, user.getId());

        Role role = roleRetrievalService.getByName(roleName);

        if (userRoleRepository.existsByUserAndRole(user, role)) {
            log.warn("[UserRoleAssignmentService::assignRoleToUser] - User ID {} already has role '{}'", user.getId(), roleName);
            throw new RoleAlreadyAssignedException();
        }

        UserRole userRole = UserRole.builder()
                .user(user)
                .role(role)
                .build();

        user.getUserRoles().add(userRole);
        userRoleRepository.save(userRole);

        log.info("[UserRoleAssignmentService::assignRoleToUser] - Role '{}' assigned to user ID {}", roleName, user.getId());
    }
}
