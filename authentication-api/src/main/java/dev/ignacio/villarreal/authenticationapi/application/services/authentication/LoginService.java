package dev.ignacio.villarreal.authenticationapi.application.services.authentication;

import dev.ignacio.villarreal.authenticationapi.application.exceptions.notFound.UserNotFoundException;
import dev.ignacio.villarreal.authenticationapi.application.exceptions.unauthorized.*;
import dev.ignacio.villarreal.authenticationapi.application.helpers.UserHelperService;
import dev.ignacio.villarreal.authenticationapi.domain.dto.auth.AuthResponse;
import dev.ignacio.villarreal.authenticationapi.domain.model.User;
import dev.ignacio.villarreal.authenticationapi.infrastructure.persistence.repositories.UserRepository;
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

    private final UserHelperService userHelperService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final AuthResponseBuilderService authResponseBuilderService;

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCKOUT_DURATION_MINUTES = 15;

    public AuthResponse login(String email, String password) {
        log.info("Authentication attempt for user: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Login failed - User with email '{}' not found", email);
                    return new UserNotFoundException();
                });

        checkTemporaryLockout(user);
        userHelperService.verifyAccountStatus(user);

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            resetLoginAttempts(user);
            log.info("Authentication successful for user: {}", email);

            return authResponseBuilderService.build(user);
        }
        catch (BadCredentialsException ex) {
            log.warn("Invalid credentials for user: {}", email);
            registerFailedLoginAttempt(user);
            throw new InvalidCredentialsException();
        }
        catch (DisabledException ex) {
            log.warn("Account disabled for user: {}", email);
            throw new AccountDisabledException();
        }
        catch (LockedException ex) {
            log.warn("Account locked for user: {}", email);
            throw new AccountLockedException();
        }
        catch (AccountExpiredException ex) {
            log.warn("Account expired for user: {}", email);
            throw new AccountExpiredCustomException();
        }
        catch (CredentialsExpiredException ex) {
            log.warn("Credentials expired for user: {}", email);
            throw new AccountCredentialsExpiredException();
        }
        catch (AuthenticationException ex) {
            log.error("Unexpected authentication error for user: {}", email, ex);
            throw new AuthenticationServiceException("Error during authentication");
        }
    }

    private void checkTemporaryLockout(User user) {
        if (user.getLockoutUntil() != null && Instant.now().isBefore(user.getLockoutUntil())) {
            log.warn("User '{}' is temporarily locked until {}", user.getEmail(), user.getLockoutUntil());
            throw new AccountLockedException();
        }
    }

    private void registerFailedLoginAttempt(User user) {
        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);

        if (attempts >= MAX_FAILED_ATTEMPTS) {
            user.setLockoutUntil(Instant.now().plus(Duration.ofMinutes(LOCKOUT_DURATION_MINUTES)));
            user.setFailedLoginAttempts(0);
            log.warn("User '{}' exceeded max login attempts. Temporarily locked for {} minutes", user.getEmail(), LOCKOUT_DURATION_MINUTES);
        }
        else {
            log.info("Failed login attempt {} for user '{}'", attempts, user.getEmail());
        }

        userRepository.save(user);
    }

    private void resetLoginAttempts(User user) {
        if (user.getFailedLoginAttempts() > 0 || user.getLockoutUntil() != null) {
            user.setFailedLoginAttempts(0);
            user.setLockoutUntil(null);
            userRepository.save(user);
            log.info("Reset failed login attempts and lockout for user '{}'", user.getEmail());
        }
    }
}
