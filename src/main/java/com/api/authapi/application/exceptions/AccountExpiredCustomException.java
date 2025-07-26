package com.api.authapi.application.exceptions;

public class AccountExpiredCustomException extends UnauthorizedException {

    public AccountExpiredCustomException() {
        super(
                "ACCOUNT_EXPIRED_EXCEPTION",
                "Account expired."
        );
    }
}
