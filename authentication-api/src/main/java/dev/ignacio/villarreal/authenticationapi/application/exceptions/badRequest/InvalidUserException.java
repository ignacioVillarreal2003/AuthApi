package dev.ignacio.villarreal.authenticationapi.application.exceptions.badRequest;

public class InvalidUserException extends BadRequestException {
    public InvalidUserException() {
        super(
                "INVALID_USER",
                "Invalid user."
        );
    }
}
