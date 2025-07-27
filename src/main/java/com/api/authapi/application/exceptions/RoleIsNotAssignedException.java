package com.api.authapi.application.exceptions;

public class RoleIsNotAssignedException extends ConflictException {
    public RoleIsNotAssignedException() {
        super(
                "ROLE_IS_NOT_ASSIGNED",
                "Role is not assigned."
        );
    }
}
