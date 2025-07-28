package com.api.authapi.application.exceptions.badRequest;

public class InvalidUserException extends BadRequestException {
    public InvalidUserException() {
        super(
                "INVALID_USER",
                "Invalid user."
        );
    }
}
