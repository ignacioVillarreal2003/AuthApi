package com.api.authapi.application.exceptions;

public class RoleAlreadyAssignedException extends ConflictException {
    public RoleAlreadyAssignedException() {
        super(
                "ROLE_ALREADY_ASSIGNED",
                "Role is already assigned."
        );
    }
}
