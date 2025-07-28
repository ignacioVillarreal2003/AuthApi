package com.api.authapi.application.helpers;

import com.api.authapi.application.exceptions.unauthorized.*;
import com.api.authapi.config.authentication.AuthenticationUserProvider;
import com.api.authapi.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserHelperService {

    private final AuthenticationUserProvider authenticatedUserProvider;

    public User getCurrentUser() {
        User user = authenticatedUserProvider.getUser();
        if (user == null) {
            throw new InvalidAuthenticationException();
        }
        return user;
    }

    public void verifyAccountStatus(User user) {
        if (!user.isEnabled()) throw new AccountDisabledException();
        if (!user.isAccountNonLocked()) throw new AccountLockedException();
        if (!user.isAccountNonExpired()) throw new AccountExpiredCustomException();
        if (!user.isCredentialsNonExpired()) throw new AccountCredentialsExpiredException();
    }
}
