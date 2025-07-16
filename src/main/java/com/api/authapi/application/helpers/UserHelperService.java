package com.api.authapi.application.helpers;

import com.api.authapi.application.exceptions.*;
import com.api.authapi.config.authentication.AuthenticationUserProvider;
import com.api.authapi.domain.constant.Role;
import com.api.authapi.domain.model.User;
import com.api.authapi.infrastructure.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserHelperService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final AuthenticationUserProvider authenticatedUserProvider;

    public User getCurrentUser() {
        log.info("[UserHelperService::getCurrentUser] Getting current authenticated user");
        User user = authenticatedUserProvider.getUser();
        if (user == null) {
            log.warn("[UserHelperService::getCurrentUser] No user found in context");
            throw new UserNotFoundException();
        }
        log.info("[UserHelperService::getCurrentUser] Current user found: {}", user.getEmail());
        return user;
    }

    public User getUserById(Long id) {
        log.info("[UserHelperService::getUserById] Looking up user by ID: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[UserHelperService::getUserById] User not found with ID: {}", id);
                    return new UserNotFoundException();
                });
    }

    public User getUserByEmail(String email) {
        log.info("[UserHelperService::getUserByEmail] Looking up user by email: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("[UserHelperService::getUserByEmail] User not found with email: {}", email);
                    return new UserNotFoundException();
                });
    }

    public void verifyAccountStatus(User user) {
        log.info("[UserHelperService::verifyAccountStatus] Verifying account status for userId={}", user.getId());
        if (!user.isEnabled()) {
            log.warn("[UserHelperService::verifyAccountStatus] Account disabled for userId={}", user.getId());
            throw new AccountDisabledException();
        }
        if (!user.isAccountNonLocked()) {
            log.warn("[UserHelperService::verifyAccountStatus] Account locked for userId={}", user.getId());
            throw new AccountLockedException();
        }
        if (!user.isAccountNonExpired()) {
            log.warn("[UserHelperService::verifyAccountStatus] Account expired for userId={}", user.getId());
            throw new CustomAccountExpiredException();
        }
        if (!user.isCredentialsNonExpired()) {
            log.warn("[UserHelperService::verifyAccountStatus] Credentials expired for userId={}", user.getId());
            throw new AccountCredentialsExpiredException();
        }
    }

    public boolean isAdmin(User user) {
        boolean isAdmin = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(Role.ROLE_ADMIN.toString()::equals);
        log.info("[UserHelperService::isAdmin] UserId={} isAdmin={}", user.getId(), isAdmin);
        return isAdmin;
    }

    public User authenticateUser(String email, String password) {
        log.info("[UserHelperService::authenticateUser] Authenticating user with email: {}", email);
        try {
            User user = getUserByEmail(email);
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            log.info("[UserHelperService::authenticateUser] User authenticated: {}", email);
            return user;
        } catch (DisabledException ex) {
            log.warn("[UserHelperService::authenticateUser] Account disabled: {}", email);
            throw new AccountDisabledException();
        } catch (LockedException ex) {
            log.warn("[UserHelperService::authenticateUser] Account locked: {}", email);
            throw new AccountLockedException();
        } catch (AccountExpiredException ex) {
            log.warn("[UserHelperService::authenticateUser] Account expired: {}", email);
            throw new CustomAccountExpiredException();
        } catch (CredentialsExpiredException ex) {
            log.warn("[UserHelperService::authenticateUser] Credentials expired: {}", email);
            throw new AccountCredentialsExpiredException();
        } catch (BadCredentialsException ex) {
            log.warn("[UserHelperService::authenticateUser] Invalid credentials for: {}", email);
            throw new InvalidCredentialsException();
        } catch (AuthenticationException ex) {
            log.warn("[UserHelperService::authenticateUser] General authentication error for: {}", email);
            throw new CustomAuthenticationException();
        }
    }
}
