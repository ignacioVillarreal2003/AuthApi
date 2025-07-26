package com.api.authapi.application.exceptions;

public class RoleAlreadyAssignedException extends ConflictException {
    public RoleAlreadyAssignedException(String name) {
        super(
                "ROLE_ALREADY_ASSIGNED",
                String.format("Role %s already assigned.", name)
        );
    }
}
