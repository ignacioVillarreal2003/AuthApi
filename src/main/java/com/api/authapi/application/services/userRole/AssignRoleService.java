package com.api.authapi.application.services.userRole;

import com.api.authapi.application.exceptions.RoleAlreadyAssignedException;
import com.api.authapi.application.services.role.RoleService;
import com.api.authapi.domain.model.Role;
import com.api.authapi.domain.model.User;
import com.api.authapi.domain.model.UserRole;
import com.api.authapi.infrastructure.persistence.repositories.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AssignRoleService {

    private final UserRoleRepository userRoleRepository;
    private final RoleService roleService;

    public void execute(User user, String roleName) {
        Role role = roleService.getRoleByName(roleName);
        if (userRoleRepository.existsByUserAndRole(user, role)) {
            throw new RoleAlreadyAssignedException(roleName);
        }
        UserRole userRole = UserRole.builder()
                .user(user)
                .role(role)
                .build();
        user.getUserRoles().add(userRole);
        userRoleRepository.save(userRole);
    }
}
