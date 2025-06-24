package com.api.authapi.application.services;

import com.api.authapi.application.mappers.UserRoleResponseMapper;
import com.api.authapi.domain.dtos.userRole.UserRoleResponse;
import com.api.authapi.domain.enums.Role;
import com.api.authapi.domain.models.User;
import com.api.authapi.domain.models.UserRole;
import com.api.authapi.infraestructure.persistence.repositories.UserRepository;
import com.api.authapi.infraestructure.persistence.repositories.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;
    private final UserRoleResponseMapper userRoleResponseMapper;

    public UserRoleResponse assignRole(Long userId, Role role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return userRoleResponseMapper.apply(createUserRole(user, role));
    }

    public UserRole createUserRole(User user, Role role) {
        if (userRoleRepository.findByUserAndRole(user, role).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Role already assigned to user");
        }

        return userRoleRepository.save(
                UserRole.builder()
                        .user(user)
                        .role(role)
                        .build()
        );
    }

    public void removeRole(Long userId, Role role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        deleteUserRoleByUserAndRole(user, role);
    }

    public void deleteUserRoleByUserAndRole(User user, Role role) {
        UserRole userRole = userRoleRepository.findByUserAndRole(user, role)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found for user"));

        userRoleRepository.delete(userRole);
    }

    public void removeAllRoles(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        deleteAllUserRoleByUser(user);
    }

    public void deleteAllUserRoleByUser(User user) {
        List<UserRole> userRoles = userRoleRepository.findAllByUser(user);

        userRoleRepository.deleteAll(userRoles);
    }
}
