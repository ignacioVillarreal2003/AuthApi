package com.api.authapi.application.exceptions;

import org.springframework.http.HttpStatus;

public abstract class ForbiddenException extends ApiException {
    public ForbiddenException(String message, String code) {
        super(message, HttpStatus.FORBIDDEN, code);
    }
}
