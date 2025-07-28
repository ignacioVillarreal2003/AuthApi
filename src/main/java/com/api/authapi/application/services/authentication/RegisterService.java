package com.api.authapi.application.services.authentication;

import com.api.authapi.application.exceptions.unauthorized.InvalidCredentialsException;
import com.api.authapi.application.helpers.MailService;
import com.api.authapi.application.helpers.UserHelperService;
import com.api.authapi.application.services.userRole.UserRoleAssignmentService;
import com.api.authapi.domain.model.User;
import com.api.authapi.domain.dto.auth.RegisterResponse;
import com.api.authapi.infrastructure.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegisterService {

    private final UserRepository userRepository;
    private final UserHelperService userHelperService;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final UserRoleAssignmentService userRoleAssignmentService;
    private final AuthResponseBuilderService authResponseBuilderService;

    public RegisterResponse register(UUID sagaId,
                                     String email,
                                     String password,
                                     List<String> roles) {
        log.info("Registering user with email: {}", email);

        Optional<User> existing = userRepository.findByEmail(email);

        if (existing.isPresent()) {
            User user = existing.get();
            log.info("User already exists, validating credentials and assigning new roles");

            validateCredentials(user, password);
            assignMissingRoles(user, roles);

            return RegisterResponse.builder()
                    .authResponse(authResponseBuilderService.build(user))
                    .isNew(false)
                    .build();
        }

        log.info("Creating new user for email: {}", email);

        User user = createUser(sagaId, email, password, roles);

        return RegisterResponse.builder()
                .authResponse(authResponseBuilderService.build(user))
                .isNew(true)
                .build();
    }

    private void validateCredentials(User user, String password) {
        userHelperService.verifyAccountStatus(user);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("Password mismatch for existing user: {}", user.getEmail());
            throw new InvalidCredentialsException();
        }
    }

    private void assignMissingRoles(User user, List<String> roles) {
        roles.stream()
                .filter(role -> user.getUserRoles().stream()
                        .noneMatch(ur -> ur.getRole().getName().equals(role)))
                .forEach(role -> {
                    log.debug("Assigning missing role '{}' to user: {}", role, user.getEmail());
                    userRoleAssignmentService.assignRoleToUser(user, role);
                });
        log.info("Role assignment completed for user: {}", user.getEmail());
    }

    private User createUser(UUID sagaId,
                            String email,
                            String password,
                            List<String> roles) {
        UUID activationToken = UUID.randomUUID();

        User user = userRepository.save(
                User.builder()
                        .email(email)
                        .password(passwordEncoder.encode(password))
                        .activationToken(activationToken)
                        .activationTokenExpiration(Instant.now().plus(Duration.ofHours(24)))
                        .build()
        );

        roles.forEach(role -> {
            log.debug("Assigning role '{}' to new user: {}", role, email);
            userRoleAssignmentService.assignRoleToUser(user, role);
        });

        mailService.sendAccountActivation(user.getEmail(), activationToken, sagaId);
        log.info("Activation email sent to: {}", email);

        return user;
    }
}
