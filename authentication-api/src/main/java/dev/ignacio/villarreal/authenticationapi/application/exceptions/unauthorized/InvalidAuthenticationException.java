package dev.ignacio.villarreal.authenticationapi.application.exceptions.unauthorized;

public class InvalidAuthenticationException extends UnauthorizedException {
    public InvalidAuthenticationException() {
        super(
                "INVALID_AUTHENTICATION",
                "Invalid authentication."
        );
    }
}
