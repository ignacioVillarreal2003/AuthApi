package com.api.authapi.application.services.authentication;

import com.api.authapi.application.exceptions.InvalidCredentialsException;
import com.api.authapi.application.helpers.MailService;
import com.api.authapi.application.helpers.UserHelperService;
import com.api.authapi.application.services.userRole.UserRoleAssignmentService;
import com.api.authapi.domain.model.User;
import com.api.authapi.domain.saga.RegisterResult;
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

    public RegisterResult register(UUID sagaId,
                                   String email,
                                   String password,
                                   List<String> roles) {
        log.info("[RegisterService::register] - Starting user registration");

        Optional<User> existing = userRepository.findByEmail(email);
        if (existing.isPresent()) {
            log.info("[RegisterService::register] - Existing user found, proceeding with role extension");
            User user = existing.get();
            registerInNewApplication(user,
                    password,
                    roles);

            log.info("[RegisterService::register] - Roles assigned for existing user");

            return RegisterResult.builder()
                    .authResponse(authResponseBuilderService.generateAuthResponse(user))
                    .isNewUser(false)
                    .build();
        }

        log.info("[RegisterService::register] - New user registration triggered");
        User user = registerNewUser(sagaId,
                email,
                password,
                roles);

        return RegisterResult.builder()
                .authResponse(authResponseBuilderService.generateAuthResponse(user))
                .isNewUser(true)
                .build();
    }

    private void registerInNewApplication(User user,
                                          String password,
                                          List<String> roles) {
        log.info("[RegisterService::registerInNewApplication] - Verifying user status");

        userHelperService.verifyAccountStatus(user);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("[RegisterService::registerInNewApplication] - Password does not match for user");
            throw new InvalidCredentialsException();
        }

        roles.stream()
                .filter(r -> user.getUserRoles().stream()
                    .noneMatch(ur -> ur.getRole().getName().equals(r)))
                .forEach(role -> {
                    log.debug("[RegisterService::registerInNewApplication] - Assigning role: {}", role);
                    userRoleAssignmentService.assignRoleToUser(user, role);
                });

        log.info("[RegisterService::registerInNewApplication] - Completed role assignment for user");
    }

    private User registerNewUser(UUID sagaId,
                                 String email,
                                 String password,
                                 List<String> roles) {
        log.info("[RegisterService::registerNewUser] - Creating new user");

        UUID token = UUID.randomUUID();

        User user = userRepository.save(
                User.builder()
                        .email(email)
                        .password(passwordEncoder.encode(password))
                        .activationToken(token)
                        .activationTokenExpiration(Instant.now().plus(Duration.ofHours(24)))
                        .build()
        );

        roles.forEach(role -> {
            log.debug("[RegisterService::registerNewUser] - Assigning role: {}", role);
            userRoleAssignmentService.assignRoleToUser(user, role);
        });

        mailService.sendActivation(user.getEmail(), token, sagaId);

        log.info("[RegisterService::registerNewUser] - Activation email sent");

        return user;
    }
}
