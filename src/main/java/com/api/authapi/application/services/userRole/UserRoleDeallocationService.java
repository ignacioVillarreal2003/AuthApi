package com.api.authapi.application.services.userRole;

import com.api.authapi.application.exceptions.RoleIsNotAssignedException;
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
public class UserRoleDeallocationService {

    private final UserRoleRepository userRoleRepository;
    private final RoleRetrievalService roleRetrievalService;
    private final UserRetrievalService userRetrievalService;

    public void deallocateRoleToUser(Long userId, String roleName) {
        log.debug("[UserRoleDeallocationService::deallocateRoleToUser] - Removing role from user");

        Role role = roleRetrievalService.getByName(roleName);
        User user = userRetrievalService.getById(userId);

        UserRole userRole = userRoleRepository.findByUserAndRole(user, role)
                .orElseThrow(() -> {
                    log.warn("[UserRoleDeallocationService::deallocateRoleToUser] - Role not assigned to user");
                    return new RoleIsNotAssignedException();
                });

        userRoleRepository.delete(userRole);
        log.info("[UserRoleDeallocationService::deallocateRoleToUser] - Role deallocated from user");
    }
}
