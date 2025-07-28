package com.api.authapi.application.services.user;

import com.api.authapi.application.exceptions.unauthorized.TokenExpiredException;
import com.api.authapi.application.exceptions.unauthorized.UserAlreadyEnabledException;
import com.api.authapi.application.exceptions.notFound.UserNotFoundException;
import com.api.authapi.application.helpers.MailService;
import com.api.authapi.application.saga.handlers.ActivateUserRegistrationHandler;
import com.api.authapi.domain.model.User;
import com.api.authapi.infrastructure.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountActivationService {

    private final UserRepository userRepository;
    private final MailService mailService;
    private final ActivateUserRegistrationHandler activateUserRegistrationHandler;


    public void reactivateAccountByToken(UUID token) {
        log.debug("Processing reactivation for token {}", token);

        User user = getUserByValidToken(token);

        if (user.isEnabled()) {
            log.info("Account already active for user {}", user.getEmail());
            return;
        }

        activateAccount(user);
        log.info("Account activated for user {}", user.getEmail());
    }

    public void verifyAccountByToken(UUID token,
                                     UUID sagaId) {
        log.debug("Attempting to activate account with token {}", token);

        User user = getUserByValidToken(token);

        if (user.isEnabled()) {
            log.info("Account already active for user {}", user.getEmail());
            return;
        }

        activateAccount(user);
        activateUserRegistrationHandler.activateUserRegistration(sagaId);

        log.info("Account activated and saga notified for user {}", user.getEmail());
    }

    private User getUserByValidToken(UUID token) {
        return userRepository.findByActivationToken(token)
                .map(user -> {
                    if (isTokenExpired(user)) {
                        log.warn("Activation token expired for user {}", user.getEmail());
                        throw new TokenExpiredException();
                    }
                    return user;
                })
                .orElseThrow(() -> {
                    log.warn("No user found with activation token {}", token);
                    return new UserNotFoundException();
                });
    }

    private boolean isTokenExpired(User user) {
        return user.getActivationTokenExpiration() == null || user.getActivationTokenExpiration().isBefore(Instant.now());
    }

    private void activateAccount(User user) {
        user.setEnabled(true);
        user.setActivationToken(null);
        user.setActivationTokenExpiration(null);
        userRepository.save(user);
    }

    public void requestAccountReactivation(String email) {
        log.debug("Reactivation requested for email {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("No user found with email {}", email);
                    return new UserNotFoundException();
                });

        if (user.isEnabled()) {
            log.info("Account already active for email {}", email);
            throw new UserAlreadyEnabledException();
        }

        UUID token = generateActivationToken(user);

        mailService.sendAccountReactivation(user.getEmail(), token);
        log.info("Reactivation email sent to {}", email);
    }

    private UUID generateActivationToken(User user) {
        UUID token = UUID.randomUUID();
        user.setActivationToken(token);
        user.setActivationTokenExpiration(Instant.now().plus(Duration.ofHours(24)));
        userRepository.save(user);
        return token;
    }
}
