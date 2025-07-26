package com.api.authapi.application.services.auth;

import com.api.authapi.application.exceptions.*;
import com.api.authapi.domain.dto.auth.AuthResponse;
import com.api.authapi.domain.dto.auth.LoginRequest;
import com.api.authapi.domain.model.User;
import com.api.authapi.infrastructure.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final BuildAuthService buildAuthService;

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCKOUT_DURATION_MINUTES = 15;

    public AuthResponse execute(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(UserNotFoundException::new);
        verifyNotLocked(user);
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password())
            );
            resetFailedAttempts(user);
            return buildAuthService.execute(user);
        } catch (BadCredentialsException ex) {
            incrementFailedAttempts(user);
            throw new InvalidCredentialsException();
        } catch (DisabledException ex) {
            throw new AccountDisabledException();
        } catch (LockedException ex) {
            throw new AccountLockedException();
        } catch (AccountExpiredException ex) {
            throw new AccountExpiredCustomException();
        } catch (CredentialsExpiredException ex) {
            throw new AccountCredentialsExpiredException();
        } catch (AuthenticationException ex) {
            throw new AuthenticationServiceException("Error during authentication");
        }
    }

    private void verifyNotLocked(User user) {
        if (user.getLockoutUntil() != null && Instant.now().isBefore(user.getLockoutUntil())) {
            throw new AccountLockedException();
        }
    }

    private void incrementFailedAttempts(User user) {
        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);
        if (attempts >= MAX_FAILED_ATTEMPTS) {
            user.setLockoutUntil(Instant.now().plus(Duration.ofMinutes(LOCKOUT_DURATION_MINUTES)));
            user.setFailedLoginAttempts(0);
        }
        userRepository.save(user);
    }

    private void resetFailedAttempts(User user) {
        if (user.getFailedLoginAttempts() > 0 || user.getLockoutUntil() != null) {
            user.setFailedLoginAttempts(0);
            user.setLockoutUntil(null);
            userRepository.save(user);
        }
    }
}
