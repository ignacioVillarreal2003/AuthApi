package dev.ignacio.villarreal.authenticationapi.application.services.userRole;

import dev.ignacio.villarreal.authenticationapi.application.exceptions.badRequest.InvalidRoleException;
import dev.ignacio.villarreal.authenticationapi.application.exceptions.badRequest.InvalidUserException;
import dev.ignacio.villarreal.authenticationapi.application.exceptions.conflict.RoleIsNotAssignedException;
import dev.ignacio.villarreal.authenticationapi.application.services.role.RoleRetrievalService;
import dev.ignacio.villarreal.authenticationapi.application.services.user.UserRetrievalService;
import dev.ignacio.villarreal.authenticationapi.domain.model.Role;
import dev.ignacio.villarreal.authenticationapi.domain.model.User;
import dev.ignacio.villarreal.authenticationapi.domain.model.UserRole;
import dev.ignacio.villarreal.authenticationapi.infrastructure.persistence.repositories.UserRoleRepository;
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
