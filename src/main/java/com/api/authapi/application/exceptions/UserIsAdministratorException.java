package com.api.authapi.application.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserIsAdministratorException extends RuntimeException {

    public UserIsAdministratorException() {
        super("User is an administrator. Cannot do this action.");
    }
}
