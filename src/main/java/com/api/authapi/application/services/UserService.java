package com.api.authapi.application.services;

import com.api.authapi.application.exceptions.InvalidCredentialsException;
import com.api.authapi.application.exceptions.UserIsAdministratorException;
import com.api.authapi.application.helpers.UserHelperService;
import com.api.authapi.domain.dtos.user.*;
import com.api.authapi.domain.models.User;
import com.api.authapi.application.mappers.UserResponseMapper;
import com.api.authapi.infraestructure.persistence.repositories.UserRepository;
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
        User user = userHelperService.getCurrentUser();
        userHelperService.verifyAccountStatus(user);

        if (request.lastPassword() != null && request.newPassword() != null) {
            if (!passwordEncoder.matches(request.lastPassword(), user.getPassword())) {
                throw new InvalidCredentialsException();
            }

            user.setPassword(passwordEncoder.encode(request.newPassword()));
        }

        userRepository.save(user);

        return userResponseMapper.apply(user);
    }

    @Transactional
    public void deleteCurrentUser() {
        User user = userHelperService.getCurrentUser();
        userHelperService.verifyAccountStatus(user);

        boolean isAdmin = userHelperService.isAdmin(user);
        if (isAdmin) {
            throw new UserIsAdministratorException();
        }

        userRepository.delete(user);
    }

    @Transactional
    public void deleteUserById(Long id) {
        User user = userHelperService.getUserById(id);
        userHelperService.verifyAccountStatus(user);

        boolean isAdmin = userHelperService.isAdmin(user);
        if (isAdmin) {
            throw new UserIsAdministratorException();
        }

        userRepository.delete(user);
    }
}
