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
        log.info("[AuthService::register] Register request received. Payload: {}", request);
        Optional<User> existing = userRepository.findByEmail(request.email());
        if (existing.isPresent()) {
            User user = existing.get();
            log.info("[AuthService::register] User already exists with email: {}. Linking roles.", request.email());
            registerUserInNewApp(user, request);
            return buildAuthResponse(user);
        }
        User user = createUser(request);
        log.info("[AuthService::register] New user created: {}", user);
        return buildAuthResponse(user);
    }

    private void registerUserInNewApp(User user, UserRegisterInitialCommand request) {
        log.debug("[AuthService::registerUserInNewApp] Linking user {} to new roles", user.getEmail());
        userHelperService.verifyAccountStatus(user);
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            log.warn("[AuthService::registerUserInNewApp] Password mismatch for user {}", user.getEmail());
            throw new InvalidCredentialsException();
        }
        request.roles().stream()
                .filter(r -> user.getRoles().stream()
                        .noneMatch(ur -> ur.getRole().getName().equals(r)))
                .forEach(role -> {
                    log.info("[AuthService::registerUserInNewApp] Assigning new role '{}' to user {}", role, user.getEmail());
                    userRoleService.assignRoleToUser(user, role);
                });
    }

    private User createUser(UserRegisterInitialCommand request) {
        log.info("[AuthService::createUser] Creating new user with email: {}", request.email());
        User user = userRepository.save(
                User.builder()
                        .email(request.email())
                        .password(passwordEncoder.encode(request.password()))
                        .build());
        request.roles().forEach(role -> {
            log.info("[AuthService::createUser] Assigning role '{}' to user {}", role, user.getEmail());
            userRoleService.assignRoleToUser(user, role);
        });
        return user;
    }

    @Transactional
    public AuthResponse login(LoginUserRequest request) {
        log.info("[AuthService::login] Login request received. Payload: {}", request);
        User user = userHelperService.authenticateUser(request.email(), request.password());
        log.info("[AuthService::login] User authenticated successfully. userId={}, email={} ", user.getId(), user.getEmail());
        return buildAuthResponse(user);
    }

    @Transactional
    public void logout() {
        log.info("[AuthService::logout] Logout requested");
        User user = userHelperService.getCurrentUser();
        userHelperService.verifyAccountStatus(user);
        user.setRefreshToken(null);
        userRepository.save(user);
        log.info("[AuthService::logout] Refresh token cleared for userId={}", user.getId());
    }

    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        log.info("[AuthService::refresh] Refresh token request received. Payload: {}", request);
        User user = userHelperService.getCurrentUser();
        userHelperService.verifyAccountStatus(user);
        if (!Objects.equals(user.getRefreshToken(), request.refreshToken())) {
            log.warn("[AuthService::refresh] Invalid refresh token for userId={}", user.getId());
            throw new InvalidRefreshTokenException();
        }
        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        log.debug("[AuthService::buildAuthResponse] Generating tokens for userId={}", user.getId());
        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        user.setRefreshToken(refreshToken);
        userRepository.save(user);
        log.info("[AuthService::buildAuthResponse] Tokens generated and saved for userId={}", user.getId());
        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .user(userResponseMapper.apply(user))
                .build();
    }
}
