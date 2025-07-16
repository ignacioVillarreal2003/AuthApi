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
        User user = authenticatedUserProvider.getUser();
        if (user == null) {
            throw new UserNotFoundException();
        }
        return user;
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
    }

    public void verifyAccountStatus(User user) {
        if (!user.isEnabled()) {
            throw new AccountDisabledException();
        }
        if (!user.isAccountNonLocked()) {
            throw new AccountLockedException();
        }
        if (!user.isAccountNonExpired()) {
            throw new CustomAccountExpiredException();
        }
        if (!user.isCredentialsNonExpired()) {
            throw new AccountCredentialsExpiredException();
        }
    }

    public boolean isAdmin(User user) {
        return user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(Role.ROLE_ADMIN.toString()::equals);
    }

    public User authenticateUser(String email, String password) {
        try {
            User user = getUserByEmail(email);
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            return user;
        } catch (DisabledException ex) {
            throw new AccountDisabledException();
        } catch (LockedException ex) {
            throw new AccountLockedException();
        } catch (AccountExpiredException ex) {
            throw new CustomAccountExpiredException();
        } catch (CredentialsExpiredException ex) {
            throw new AccountCredentialsExpiredException();
        } catch (BadCredentialsException ex) {
            throw new InvalidCredentialsException();
        } catch (AuthenticationException ex) {
            throw new CustomAuthenticationException();
        }
    }
}
