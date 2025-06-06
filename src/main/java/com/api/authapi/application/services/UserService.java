package com.api.authapi.application.services;

import com.api.authapi.config.AuthenticatedUserProvider;
import com.api.authapi.config.JwtService;
import com.api.authapi.domain.dtos.user.*;
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
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        Optional<User> existing = userRepository.findByEmail(request.email());

        if (existing.isPresent()) {
            User user = existing.get();

            if (!user.isEnabled()) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is disabled");
            }

            if (!passwordEncoder.matches(request.password(), user.getPassword())) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Passwords do not match");
            }

            List<Role> newRoles = request.roles()
                    .stream()
                    .filter(role -> !user.getRoles().contains(role))
                    .toList();

            if (newRoles.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists with the same roles");
            }

            newRoles.forEach(role -> {
                userRoleService.createUserRole(user, role);
            });

            userRepository.save(user);

            return getAuthResponse(user);
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

        return getAuthResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );
        }
        catch (BadCredentialsException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        User user = userRepository.findByEmailAndEnabledTrue(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return getAuthResponse(user);
    }

    public void logout() {
        User existingUser = authenticatedUserProvider.getAuthenticatedUser();

        existingUser.setRefreshToken(null);

        userRepository.save(existingUser);
    }

    public AuthResponse refresh(RefreshRequest request) {
        User existingUser = authenticatedUserProvider.getAuthenticatedUser();

        if (!existingUser.getRefreshToken().equals(request.refreshToken())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token is incorrect");
        }

        return getAuthResponse(existingUser);
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

    private AuthResponse getAuthResponse(User user) {
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
