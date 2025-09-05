package dev.ignacio.villarreal.authenticationapi.application.exceptions.notFound;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException() {
        super(
                "USER_NOT_FOUND",
                "User not found."
        );
    }
}
