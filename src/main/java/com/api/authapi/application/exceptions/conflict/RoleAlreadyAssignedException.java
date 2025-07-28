package com.api.authapi.application.exceptions.conflict;

public class RoleAlreadyAssignedException extends ConflictException {
    public RoleAlreadyAssignedException() {
        super(
                "ROLE_ALREADY_ASSIGNED",
                "Role is already assigned."
        );
    }
}
