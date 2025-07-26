package com.api.authapi.application.exceptions;

public class InvalidRefreshTokenException extends UnauthorizedException {
    public InvalidRefreshTokenException() {
        super(
                "INVALID_CREDENTIALS",
                "Invalid refresh token."
        );
    }
}
