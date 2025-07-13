package com.api.authapi.application.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class RoleAlreadyAssignedException extends RuntimeException {

    public RoleAlreadyAssignedException() {
        super("Role already assigned to user");
    }
}
