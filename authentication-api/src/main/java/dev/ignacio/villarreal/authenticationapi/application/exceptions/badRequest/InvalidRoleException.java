package dev.ignacio.villarreal.authenticationapi.application.exceptions.badRequest;

public class InvalidRoleException extends BadRequestException {
    public InvalidRoleException() {
        super(
                "INVALID_ROLE",
                "Invalid role."
        );
    }
}
