package com.api.authapi.application.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class CustomAccountExpiredException extends RuntimeException {

    public CustomAccountExpiredException() {
        super("Account expired");
    }
}
