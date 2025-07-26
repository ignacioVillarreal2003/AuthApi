package com.api.authapi.application.exceptions;

import org.springframework.http.HttpStatus;

public abstract class ConflictException extends ApiException {
    public ConflictException(String message, String code) {
        super(message, HttpStatus.NOT_FOUND, code);
    }
}
