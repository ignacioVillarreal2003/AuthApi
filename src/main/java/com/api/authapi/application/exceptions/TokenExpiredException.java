package com.api.authapi.application.exceptions;

public class TokenExpiredException extends UnauthorizedException {
    public TokenExpiredException() {
        super(
                "TOKEN_EXPIRED",
                "Token is expired."
        );
    }
}
