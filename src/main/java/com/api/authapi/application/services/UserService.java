package com.api.authapi.application.services;

import com.api.authapi.application.exceptions.InvalidCredentialsException;
import com.api.authapi.application.exceptions.UserIsAdministratorException;
import com.api.authapi.application.helpers.UserHelperService;
import com.api.authapi.domain.dto.user.*;
import com.api.authapi.domain.model.User;
import com.api.authapi.application.mappers.UserResponseMapper;
import com.api.authapi.infrastructure.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserHelperService userHelperService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserResponseMapper userResponseMapper;

    @Transactional
    public UserResponse updateCurrentUser(UpdateUserRequest request) {
        log.info("[UserService::updateCurrentUser] Update request received. Payload: {}", request);
        User user = userHelperService.getCurrentUser();
        userHelperService.verifyAccountStatus(user);
        if (request.lastPassword() != null && request.newPassword() != null) {
            log.info("[UserService::updateCurrentUser] Password update requested for userId={}", user.getId());
            if (!passwordEncoder.matches(request.lastPassword(), user.getPassword())) {
                log.warn("[UserService::updateCurrentUser] Invalid current password for userId={}", user.getId());
                throw new InvalidCredentialsException();
            }
            user.setPassword(passwordEncoder.encode(request.newPassword()));
        }
        userRepository.save(user);
        log.info("[UserService::updateCurrentUser] User updated successfully. userId={}", user.getId());
        return userResponseMapper.apply(user);
    }

    @Transactional
    public void deleteCurrentUser() {
        log.info("[UserService::deleteCurrentUser] Delete current user request");
        User user = userHelperService.getCurrentUser();
        userHelperService.verifyAccountStatus(user);
        if (userHelperService.isAdmin(user)) {
            log.warn("[UserService::deleteCurrentUser] Cannot delete admin user. userId={}", user.getId());
            throw new UserIsAdministratorException();
        }
        userRepository.delete(user);
        log.info("[UserService::deleteCurrentUser] User deleted. userId={}", user.getId());
    }

    @Transactional
    public void deleteUserById(Long id) {
        log.info("[UserService::deleteUserById] Delete user request. userId={}", id);
        User user = userHelperService.getUserById(id);
        userHelperService.verifyAccountStatus(user);
        if (userHelperService.isAdmin(user)) {
            log.warn("[UserService::deleteUserById] Cannot delete admin user. userId={}", id);
            throw new UserIsAdministratorException();
        }
        userRepository.delete(user);
        log.info("[UserService::deleteUserById] User deleted. userId={}", id);
    }
}
