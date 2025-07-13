package com.api.authapi.application.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AccountCredentialsExpiredException extends RuntimeException {

    public AccountCredentialsExpiredException() {
        super("Account credentials expired");
    }
}
