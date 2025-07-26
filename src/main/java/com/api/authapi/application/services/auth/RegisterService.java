package com.api.authapi.application.services.auth;

import com.api.authapi.application.exceptions.InvalidCredentialsException;
import com.api.authapi.application.helpers.EmailService;
import com.api.authapi.application.helpers.UserHelperService;
import com.api.authapi.application.services.userRole.AssignRoleService;
import com.api.authapi.domain.dto.auth.AuthResponse;
import com.api.authapi.domain.model.User;
import com.api.authapi.domain.saga.RegisterResult;
import com.api.authapi.domain.saga.command.InitiateUserRegistrationCommand;
import com.api.authapi.infrastructure.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final UserRepository userRepository;
    private final UserHelperService userHelperService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AssignRoleService assignRoleService;
    private final BuildAuthService buildAuthService;

    public RegisterResult execute(InitiateUserRegistrationCommand request, UUID sagaId) {
        Optional<User> existing = userRepository.findByEmail(request.email());
        if (existing.isPresent()) {
            User user = existing.get();
            registerUserInNewApp(user, request);
            return RegisterResult.builder()
                    .authResponse(buildAuthService.execute(user))
                    .isNewUser(true)
                    .build();
        }
        User user = createUser(request, sagaId);
        return RegisterResult.builder()
                .authResponse(buildAuthService.execute(user))
                .isNewUser(false)
                .build();
    }

    private void registerUserInNewApp(User user, InitiateUserRegistrationCommand request) {
        userHelperService.verifyAccountStatus(user);
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }
        request.roles().stream()
                .filter(r -> user.getUserRoles().stream()
                        .noneMatch(ur -> ur.getRole().getName().equals(r)))
                .forEach(role -> {
                    assignRoleService.execute(user, role);
                });
    }

    private User createUser(InitiateUserRegistrationCommand request, UUID sagaId) {
        UUID token = UUID.randomUUID();
        User user = userRepository.save(
                User.builder()
                        .email(request.email())
                        .password(passwordEncoder.encode(request.password()))
                        .activationToken(token)
                        .activationTokenExpiration(Instant.now().plus(Duration.ofHours(24)))
                        .build());
        request.roles().forEach(role -> {
            assignRoleService.execute(user, role);
        });
        emailService.sendActivationEmail(user.getEmail(), token, sagaId);
        return user;
    }
}
