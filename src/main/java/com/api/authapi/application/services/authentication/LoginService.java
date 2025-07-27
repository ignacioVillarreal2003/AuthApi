package com.api.authapi.application.services.authentication;

import com.api.authapi.application.exceptions.*;
import com.api.authapi.domain.dto.auth.AuthResponse;
import com.api.authapi.domain.model.User;
import com.api.authapi.infrastructure.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final AuthResponseBuilderService authResponseBuilderService;
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCKOUT_DURATION_MINUTES = 15;

    public AuthResponse login(String email, String password) {
        log.info("[LoginService::login] - Attempting authentication");

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("[LoginService::login] - User not found");
                    return new UserNotFoundException();
                });

        verifyNotTemporarilyLocked(user);

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            resetLoginAttempts(user);

            log.info("[LoginService::login] - Authentication successful");
            return authResponseBuilderService.generateAuthResponse(user);
        }
        catch (BadCredentialsException ex) {
            log.warn("[LoginService::login] - Invalid credentials");
            registerFailedLoginAttempt(user);
            throw new InvalidCredentialsException();
        }
        catch (DisabledException ex) {
            log.warn("[LoginService::login] - Account disabled");
            throw new AccountDisabledException();
        }
        catch (LockedException ex) {
            log.warn("[LoginService::login] - Account locked");
            throw new AccountLockedException();
        }
        catch (AccountExpiredException ex) {
            log.warn("[LoginService::login] - Account expired");
            throw new AccountExpiredCustomException();
        }
        catch (CredentialsExpiredException ex) {
            log.warn("[LoginService::login] - Credentials expired");
            throw new AccountCredentialsExpiredException();
        }
        catch (AuthenticationException ex) {
            log.error("[LoginService::login] - General authentication failure");
            throw new AuthenticationServiceException("Error during authentication");
        }
    }

    private void verifyNotTemporarilyLocked(User user) {
        if (user.getLockoutUntil() != null && Instant.now().isBefore(user.getLockoutUntil())) {
            log.warn("[LoginService::verifyNotTemporarilyLocked] - User temporarily locked");
            throw new AccountLockedException();
        }
    }

    private void registerFailedLoginAttempt(User user) {
        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);

        if (attempts >= MAX_FAILED_ATTEMPTS) {
            user.setLockoutUntil(Instant.now().plus(Duration.ofMinutes(LOCKOUT_DURATION_MINUTES)));
            user.setFailedLoginAttempts(0);
            log.warn("[LoginService::registerFailedLoginAttempt] - Max attempts exceeded, user locked");
        }
        else {
            log.info("[LoginService::registerFailedLoginAttempt] - Failed attempt registered");
        }

        userRepository.save(user);
    }

    private void resetLoginAttempts(User user) {
        if (user.getFailedLoginAttempts() > 0 || user.getLockoutUntil() != null) {
            user.setFailedLoginAttempts(0);
            user.setLockoutUntil(null);
            userRepository.save(user);
            log.info("[AuthenticationService::resetLoginAttempts] - Reset failed attempts and lockout");
        }
    }
}
