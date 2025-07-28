package com.api.authapi.application.exceptions.badRequest;

public class InvalidRoleException extends BadRequestException {
    public InvalidRoleException() {
        super(
                "INVALID_ROLE",
                "Invalid role."
        );
    }
}
