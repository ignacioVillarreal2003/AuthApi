package com.api.authapi.application.services;

import com.api.authapi.application.exceptions.RoleAlreadyAssignedException;
import com.api.authapi.domain.model.Role;
import com.api.authapi.domain.model.User;
import com.api.authapi.domain.model.UserRole;
import com.api.authapi.infrastructure.persistence.repositories.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;
    private final RoleService roleService;

    @Transactional
    public void assignRoleToUser(User user, String roleName) {
        Role role = roleService.getRoleByName(roleName);

        if (userRoleRepository.existsByUserAndRole(user, role)) {
            throw new RoleAlreadyAssignedException();
        }

        UserRole userRole = UserRole.builder()
                .user(user)
                .role(role)
                .build();

        user.getRoles().add(userRole);
        userRoleRepository.save(userRole);
    }
}
