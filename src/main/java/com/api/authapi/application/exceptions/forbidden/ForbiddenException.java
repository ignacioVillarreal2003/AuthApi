package com.api.authapi.application.exceptions.forbidden;

import com.api.authapi.application.exceptions.ApiException;
import org.springframework.http.HttpStatus;

public abstract class ForbiddenException extends ApiException {
    public ForbiddenException(String message, String code) {
        super(message, HttpStatus.FORBIDDEN, code);
    }
}
