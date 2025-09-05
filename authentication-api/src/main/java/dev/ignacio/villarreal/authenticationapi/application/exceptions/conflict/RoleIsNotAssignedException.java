package dev.ignacio.villarreal.authenticationapi.application.exceptions.conflict;

public class RoleIsNotAssignedException extends ConflictException {
    public RoleIsNotAssignedException() {
        super(
                "ROLE_IS_NOT_ASSIGNED",
                "Role is not assigned."
        );
    }
}
