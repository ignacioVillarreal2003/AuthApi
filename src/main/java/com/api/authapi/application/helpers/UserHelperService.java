package com.api.authapi.application.helpers;

import com.api.authapi.application.exceptions.*;
import com.api.authapi.application.exceptions.AccountExpiredException;
import com.api.authapi.config.authentication.AuthenticationUserProvider;
import com.api.authapi.domain.model.User;
import com.api.authapi.infrastructure.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

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
            throw new AccountExpiredException();
        }
        if (!user.isCredentialsNonExpired()) {
            throw new AccountCredentialsExpiredException();
        }
    }

    public boolean isAdmin(User user) {
        return user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);
    }

    public void authenticateUser(String email, String password) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
        } catch (DisabledException ex) {
            throw new AccountDisabledException();
        } catch (LockedException ex) {
            throw new AccountLockedException();
        } catch (AccountExpiredException ex) {
            throw new com.api.authapi.application.exceptions.AccountExpiredException();
        } catch (CredentialsExpiredException ex) {
            throw new AccountCredentialsExpiredException();
        } catch (BadCredentialsException ex) {
            throw new InvalidCredentialsException();
        }
    }
}
