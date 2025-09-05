package dev.ignacio.villarreal.authenticationapi.config.annotations;

import dev.ignacio.villarreal.authenticationapi.application.exceptions.unauthorized.AccountCredentialsExpiredException;
import dev.ignacio.villarreal.authenticationapi.application.exceptions.unauthorized.AccountDisabledException;
import dev.ignacio.villarreal.authenticationapi.application.exceptions.unauthorized.AccountExpiredCustomException;
import dev.ignacio.villarreal.authenticationapi.application.exceptions.unauthorized.AccountLockedException;
import dev.ignacio.villarreal.authenticationapi.application.helpers.UserHelperService;
import dev.ignacio.villarreal.authenticationapi.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class AccountStatusAspect {

    private final UserHelperService userHelperService;

    @Before("@annotation(com.api.authapi.config.annotations.RequireActiveAccount)")
    public void checkAccountStatus() {
        User currentUser = userHelperService.getCurrentUser();
        if (!currentUser.isEnabled()) throw new AccountDisabledException();
        if (!currentUser.isAccountNonLocked()) throw new AccountLockedException();
        if (!currentUser.isAccountNonExpired()) throw new AccountExpiredCustomException();
        if (!currentUser.isCredentialsNonExpired()) throw new AccountCredentialsExpiredException();
    }
}
