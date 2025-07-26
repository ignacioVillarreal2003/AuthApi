package com.api.authapi.application.exceptions;

public class CustomAuthenticationException extends BadRequestException {
    public CustomAuthenticationException() {
        super(
                "AUTHENTICATION",
                "Authentication failed"
        );
    }
}
