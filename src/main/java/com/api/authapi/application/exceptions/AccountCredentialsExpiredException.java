package com.api.authapi.application.exceptions;

public class AccountCredentialsExpiredException extends UnauthorizedException {
    public AccountCredentialsExpiredException() {
        super(
                "ACCOUNT_CREDENTIALS_EXPIRED",
                "Account credentials expired."
        );
    }
}
