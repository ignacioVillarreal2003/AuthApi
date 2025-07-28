package com.api.authapi.application.exceptions.unauthorized;

public class AccountExpiredCustomException extends UnauthorizedException {

    public AccountExpiredCustomException() {
        super(
                "ACCOUNT_EXPIRED_EXCEPTION",
                "Account expired."
        );
    }
}
