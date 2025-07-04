package com.api.authapi.application.services;

import com.api.authapi.application.helpers.AuthHelper;
import com.api.authapi.config.authentication.AuthenticationUserProvider;
import com.api.authapi.domain.dtos.user.*;
import com.api.authapi.domain.enums.Role;
import com.api.authapi.domain.models.User;
import com.api.authapi.application.mappers.UserResponseMapper;
import com.api.authapi.infraestructure.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserRoleService userRoleService;
    private final PasswordEncoder passwordEncoder;
    private final UserResponseMapper userResponseMapper;
    private final AuthenticationUserProvider authenticatedUserProvider;
    private final AuthHelper authHelper;

    @Transactional
    public AuthResponse register(UserRegisterCommand request) {
        Optional<User> existing = userRepository.findByEmail(request.email());

        if (existing.isPresent()) {
            User user = existing.get();

            if (!user.isEnabled()) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is disabled");
            }

            if (!passwordEncoder.matches(request.password(), user.getPassword())) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Passwords do not match");
            }

            List<Role> newRoles = request.roles().stream()
                    .filter(role -> user.getRoles().stream()
                            .anyMatch(userRole -> userRole.getRole().equals(role)))
                    .toList();

            if (newRoles.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists with the same roles");
            }

            newRoles.forEach(role -> {
                userRoleService.createUserRole(user, role);
            });

            userRepository.save(user);

            return authHelper.getAuthResponse(user.getId());
        }

        User user = userRepository.save(
                User.builder().email(request.email())
                        .password(passwordEncoder.encode(request.password()))
                        .refreshToken(null)
                        .roles(new ArrayList<>())
                        .build()
        );

        request.roles().forEach(role -> {
            userRoleService.createUserRole(user, role);
        });

        return authHelper.getAuthResponse(user.getId());
    }

    public UserResponse updateUser(UpdateUserRequest request) {
        User existingUser = authenticatedUserProvider.getAuthenticatedUser();

        if (request.lastPassword() != null && request.newPassword() != null) {
            if (!passwordEncoder.matches(request.lastPassword(), existingUser.getPassword())) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Passwords do not match");
            }

            existingUser.setPassword(passwordEncoder.encode(request.newPassword()));
        }

        userRepository.save(existingUser);

        return userResponseMapper.apply(existingUser);
    }

    public void enableUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        user.setEnabled(true);

        userRepository.save(user);
    }

    public void disableUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        user.setEnabled(false);

        userRepository.save(user);
    }

    @Transactional
    public void deleteUserPermanently(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        userRoleService.deleteAllUserRoleByUser(user);

        userRepository.delete(user);
    }
}
