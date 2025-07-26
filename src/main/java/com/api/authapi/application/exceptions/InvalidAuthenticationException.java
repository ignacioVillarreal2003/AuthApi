package com.api.authapi.application.exceptions;

public class InvalidAuthenticationException extends UnauthorizedException {
    public InvalidAuthenticationException() {
        super(
                "INVALID_AUTHENTICATION",
                "Invalid authentication."
        );
    }
}
