package dev.ignacio.villarreal.authenticationapi.application.exceptions.unauthorized;

public class InvalidCredentialsException extends UnauthorizedException {
    public InvalidCredentialsException() {
        super(
                "INVALID_CREDENTIALS",
                "Invalid email or password."
        );
    }
}
