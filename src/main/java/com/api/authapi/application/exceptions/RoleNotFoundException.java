package com.api.authapi.application.exceptions;

public class RoleNotFoundException extends NotFoundException {
    public RoleNotFoundException() {
        super(
                "ROLE_NOT_FOUND",
                "Role not found."
        );
    }
}