package com.api.authapi.application.exceptions;

public class AccountDisabledException extends UnauthorizedException {
    public AccountDisabledException() {
        super(
                "ACCOUNT_DISABLED",
                "Account disabled."
        );
    }
}