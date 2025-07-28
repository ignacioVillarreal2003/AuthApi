package com.api.authapi.application.services.userRole;

import com.api.authapi.application.exceptions.badRequest.InvalidRoleException;
import com.api.authapi.application.exceptions.badRequest.InvalidUserException;
import com.api.authapi.application.exceptions.conflict.RoleIsNotAssignedException;
import com.api.authapi.application.services.role.RoleRetrievalService;
import com.api.authapi.application.services.user.UserRetrievalService;
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
public class UserRoleRevocationService {

    private final UserRoleRepository userRoleRepository;
    private final RoleRetrievalService roleRetrievalService;
    private final UserRetrievalService userRetrievalService;

    public void removeRoleFromUser(Long userId, String roleName) {
        log.debug("Attempting to remove role '{}' from user ID {}", roleName, userId);

        if (userId == null) {
            log.error("User ID is null during role removal");
            throw new InvalidUserException();
        }

        if (roleName == null || roleName.isBlank()) {
            log.error("Invalid role name '{}' during role removal for user ID {}", roleName, userId);
            throw new InvalidRoleException();
        }

        Role role = roleRetrievalService.getByName(roleName);
        User user = userRetrievalService.getById(userId);

        log.debug("Retrieved role '{}' and user '{}'", role.getName(), user.getEmail());

        UserRole userRole = userRoleRepository.findByUserAndRole(user, role)
                .orElseThrow(() -> {
                    log.warn("Role '{}' is not assigned to user {}", roleName, user.getEmail());
                    return new RoleIsNotAssignedException();
                });

        userRoleRepository.delete(userRole);
        log.info("Role '{}' successfully removed from user {}", roleName, user.getEmail());
    }
}
