package com.api.authapi.application.services.user;

import com.api.authapi.application.exceptions.TokenExpiredException;
import com.api.authapi.application.exceptions.UserAlreadyEnabledException;
import com.api.authapi.application.exceptions.UserNotFoundException;
import com.api.authapi.application.helpers.MailService;
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

    public void activateAccount(UUID token) {
        log.debug("[AccountActivationService::activateAccount] - Activating account");
                User user = userRepository.findByActivationToken(token)
                        .orElseThrow(() -> {
                            log.warn("[AccountActivationService::activateAccount] - Token not associated with any user");
                            return new UserNotFoundException();
                        });

        if (user.getActivationTokenExpiration().isBefore(Instant.now())) {
            log.warn("[AccountActivationService::activateAccount] - Token expired");
            throw new TokenExpiredException();
        }

        if (!user.isEnabled()) {
            user.setEnabled(true);
            user.setActivationToken(null);
            user.setActivationTokenExpiration(null);
            userRepository.save(user);
            log.info("[AccountActivationService::activateAccount] - Account activated");
        } else {
            log.info("[AccountActivationService::activateAccount] - Account already active");
        }
    }

    public void requestAccountReactivation(String email) {
        log.debug("[AccountActivationService::requestAccountReactivation] - Reactivation requested");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("[AccountActivationService::requestAccountReactivation] - No user found");
                    return new UserNotFoundException();
                });

        if (user.isEnabled()) {
            log.info("[AccountActivationService::requestAccountReactivation] - Account already active");
            throw new UserAlreadyEnabledException();
        }

        UUID token = UUID.randomUUID();
        user.setActivationToken(token);
        user.setActivationTokenExpiration(Instant.now().plus(Duration.ofHours(24)));
        userRepository.save(user);

        mailService.sendReactivation(user.getEmail(), token);
        log.info("[AccountActivationService::requestAccountReactivation] - Reactivation email sent");
    }
}
