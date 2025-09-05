package dev.ignacio.villarreal.authenticationapi.application.exceptions.unauthorized;

public class AccountDisabledException extends UnauthorizedException {
    public AccountDisabledException() {
        super(
                "ACCOUNT_DISABLED",
                "Account disabled."
        );
    }
}