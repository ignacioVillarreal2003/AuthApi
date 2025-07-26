package com.api.authapi.application.exceptions;

public class InvalidCredentialsException extends UnauthorizedException {
    public InvalidCredentialsException() {
        super(
                "INVALID_CREDENTIALS",
                "Invalid email or password."
        );
    }
}
