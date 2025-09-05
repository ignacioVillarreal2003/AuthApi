package dev.ignacio.villarreal.authenticationapi.application.exceptions.conflict;

public class PasswordAlreadyUsedException extends ConflictException {
    public PasswordAlreadyUsedException() {
        super(
                "PASSWORD_ALREADY_USED",
                "Password already used."
        );
    }
}
