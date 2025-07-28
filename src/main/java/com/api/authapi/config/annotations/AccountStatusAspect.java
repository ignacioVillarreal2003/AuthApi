package com.api.authapi.config.annotations;

import com.api.authapi.application.exceptions.unauthorized.AccountCredentialsExpiredException;
import com.api.authapi.application.exceptions.unauthorized.AccountDisabledException;
import com.api.authapi.application.exceptions.unauthorized.AccountExpiredCustomException;
import com.api.authapi.application.exceptions.unauthorized.AccountLockedException;
import com.api.authapi.application.helpers.UserHelperService;
import com.api.authapi.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class AccountStatusAspect {

    private final UserHelperService userHelperService;

    @Before("@annotation(RequireActiveAccount)")
    public void checkAccountStatus() {
        User currentUser = userHelperService.getCurrentUser();
        if (!currentUser.isEnabled()) throw new AccountDisabledException();
        if (!currentUser.isAccountNonLocked()) throw new AccountLockedException();
        if (!currentUser.isAccountNonExpired()) throw new AccountExpiredCustomException();
        if (!currentUser.isCredentialsNonExpired()) throw new AccountCredentialsExpiredException();
    }
}
