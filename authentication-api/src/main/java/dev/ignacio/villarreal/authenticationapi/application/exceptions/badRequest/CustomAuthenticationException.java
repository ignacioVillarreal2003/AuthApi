package dev.ignacio.villarreal.authenticationapi.application.exceptions.badRequest;

public class CustomAuthenticationException extends BadRequestException {
    public CustomAuthenticationException() {
        super(
                "AUTHENTICATION",
                "Authentication failed."
        );
    }
}
