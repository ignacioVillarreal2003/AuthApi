package com.api.authapi.application.services;

import com.api.authapi.application.exceptions.InvalidCredentialsException;
import com.api.authapi.application.exceptions.InvalidRefreshTokenException;
import com.api.authapi.application.helpers.UserHelperService;
import com.api.authapi.application.mappers.UserResponseMapper;
import com.api.authapi.config.authentication.JwtService;
import com.api.authapi.domain.dto.auth.AuthResponse;
import com.api.authapi.domain.dto.auth.LoginUserRequest;
import com.api.authapi.domain.dto.auth.RefreshTokenRequest;
import com.api.authapi.domain.saga.command.UserRegisterInitialCommand;
import com.api.authapi.domain.model.User;
import com.api.authapi.infrastructure.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserResponseMapper userResponseMapper;
    private final UserHelperService userHelperService;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleService userRoleService;

    @Transactional
    public AuthResponse register(UserRegisterInitialCommand request) {
        Optional<User> existing = userRepository.findByEmail(request.email());

        if (existing.isPresent()) {
            User user = existing.get();

            registerUserInNewApp(user, request);

            return buildAuthResponse(user);
        }

        User newUser = createUser(request);

        return buildAuthResponse(newUser);
    }

    private void registerUserInNewApp(User user, UserRegisterInitialCommand request) {
        userHelperService.verifyAccountStatus(user);

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        List<String> newRoles = request.roles().stream()
                .filter(r -> user.getRoles().stream()
                        .noneMatch(ur -> ur.getRole().getName().equals(r)))
                .toList();

        newRoles.forEach(role -> {
            userRoleService.assignRoleToUser(user, role);
        });
    }

    private User createUser(UserRegisterInitialCommand request) {
        User user = userRepository.save(
                User.builder()
                        .email(request.email())
                        .password(passwordEncoder.encode(request.password()))
                        .build()
        );

        request.roles().forEach(role -> {
            userRoleService.assignRoleToUser(user, role);
        });

        return user;
    }

    @Transactional
    public AuthResponse login(LoginUserRequest request) {
        userHelperService.authenticateUser(request.email(), request.password());

        User user = userHelperService.getUserByEmail(request.email());

        return buildAuthResponse(user);
    }

    @Transactional
    public void logout() {
        User user = userHelperService.getCurrentUser();
        userHelperService.verifyAccountStatus(user);

        user.setRefreshToken(null);

        userRepository.save(user);
    }

    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        User user = userHelperService.getCurrentUser();
        userHelperService.verifyAccountStatus(user);

        if (!Objects.equals(user.getRefreshToken(), request.refreshToken())) {
            throw new InvalidRefreshTokenException();
        }

        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return AuthResponse.builder()
                .token(token)
                .refreshToken(user.getRefreshToken())
                .user(userResponseMapper.apply(user))
                .build();
    }
}
