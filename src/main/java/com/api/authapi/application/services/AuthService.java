package com.api.authapi.application.services;

import com.api.authapi.config.AuthenticatedUserProvider;
import com.api.authapi.config.JwtService;
import com.api.authapi.domain.dtos.*;
import com.api.authapi.domain.enums.Role;
import com.api.authapi.domain.models.User;
import com.api.authapi.application.mappers.UserResponseMapper;
import com.api.authapi.infraestructure.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserResponseMapper userResponseMapper;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    public AuthResponse register(RegisterRequest request) {
        log.info("Attempting to register user with email: {}", request.email());

        if (userRepository.findByEmail(request.email()).isPresent()) {
            log.warn("Registration failed: User with email {} already exists", request.email());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists");
        }

        User newUser = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .refreshToken(null)
                .role(Role.USER)
                .build();

        userRepository.save(newUser);

        log.info("User registered successfully with email: {}", newUser.getEmail());

        return getAuthResponse(newUser);
    }

    public AuthResponse login(LoginRequest request) {
        log.info("User attempting to login with email: {}", request.email());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );
        } catch (BadCredentialsException ex) {
            log.warn("Login failed for user {}: Bad credentials", request.email());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        User existingUser = userRepository.findByEmail(request.email())
                .orElseThrow(() -> {
                    log.error("Login failed: User with email {} not found", request.email());
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
                });

        log.info("User logged in successfully: {}", existingUser.getEmail());

        return getAuthResponse(existingUser);
    }

    public void logout() {
        User existingUser = authenticatedUserProvider.getAuthenticatedUser();

        log.info("User {} is logging out", existingUser.getEmail());

        existingUser.setRefreshToken(null);
        userRepository.save(existingUser);

        log.info("User {} successfully logged out", existingUser.getEmail());
    }

    public AuthResponse refresh(RefreshRequest request) {
        User existingUser = authenticatedUserProvider.getAuthenticatedUser();

        log.debug("Attempting to refresh token for user: {}", existingUser.getEmail());

        if (!existingUser.getRefreshToken().equals(request.refreshToken())) {
            log.warn("Refresh token mismatch for user: {}", existingUser.getEmail());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token is invalid");
        }

        log.info("Token refreshed successfully for user: {}", existingUser.getEmail());

        return getAuthResponse(existingUser);
    }

    public UserResponse updateUser(UpdateUserRequest request) {
        User existingUser = authenticatedUserProvider.getAuthenticatedUser();
        log.info("User {} is attempting to update account", existingUser.getEmail());

        if (request.lastPassword() != null && request.newPassword() != null) {
            log.debug("Checking current password for user: {}", existingUser.getEmail());

            if (!passwordEncoder.matches(request.lastPassword(), existingUser.getPassword())) {
                log.warn("Password mismatch for user: {}", existingUser.getEmail());
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The password doesn't match");
            }
            existingUser.setPassword(passwordEncoder.encode(request.newPassword()));
            log.info("Password updated successfully for user: {}", existingUser.getEmail());
        }

        userRepository.save(existingUser);

        log.info("User {} updated account successfully", existingUser.getEmail());

        return userResponseMapper.apply(existingUser);
    }

    public void deleteUser() {
        User existingUser = authenticatedUserProvider.getAuthenticatedUser();

        log.info("User {} is deleting their account", existingUser.getEmail());

        userRepository.delete(existingUser);

        log.info("User {} deleted their account successfully", existingUser.getEmail());
    }

    private AuthResponse getAuthResponse(User user) {
        log.debug("Generating tokens for user: {}", user.getEmail());

        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        log.debug("Tokens generated and user updated: {}", user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .refreshToken(user.getRefreshToken())
                .user(userResponseMapper.apply(user))
                .build();
    }
}
