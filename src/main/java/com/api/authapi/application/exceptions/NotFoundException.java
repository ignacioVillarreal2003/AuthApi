package com.api.authapi.application.exceptions;

import org.springframework.http.HttpStatus;

public abstract class NotFoundException extends ApiException {
    public NotFoundException(String message, String code) {
        super(message, HttpStatus.NOT_FOUND, code);
    }
}