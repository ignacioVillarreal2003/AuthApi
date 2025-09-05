package dev.ignacio.villarreal.authenticationapi.application.services.userRole;

import dev.ignacio.villarreal.authenticationapi.application.exceptions.badRequest.InvalidRoleException;
import dev.ignacio.villarreal.authenticationapi.application.exceptions.badRequest.InvalidUserException;
import dev.ignacio.villarreal.authenticationapi.application.exceptions.conflict.RoleAlreadyAssignedException;
import dev.ignacio.villarreal.authenticationapi.application.services.role.RoleRetrievalService;
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
public class UserRoleAssignmentService {

    private final UserRoleRepository userRoleRepository;
    private final RoleRetrievalService roleRetrievalService;

    public void assignRoleToUser(User user, String roleName) {
        log.debug("Attempting to assign role '{}' to user {}", roleName, user != null ? user.getEmail() : null);

        if (user == null) {
            log.error("User is null during role assignment");
            throw new InvalidUserException();
        }

        if (roleName == null || roleName.isBlank()) {
            log.error("Invalid role name '{}' for user {}", roleName, user.getEmail());
            throw new InvalidRoleException();
        }

        Role role = roleRetrievalService.getByName(roleName);
        log.debug("Retrieved role '{}' for assignment to user {}", role.getName(), user.getEmail());

        if (userRoleRepository.existsByUserAndRole(user, role)) {
            log.warn("Role '{}' already assigned to user {}", roleName, user.getEmail());
            throw new RoleAlreadyAssignedException();
        }

        UserRole userRole = UserRole.builder()
                .user(user)
                .role(role)
                .build();

        user.getUserRoles().add(userRole);
        userRoleRepository.save(userRole);

        log.info("Role '{}' successfully assigned to user {}", roleName, user.getEmail());
    }
}
