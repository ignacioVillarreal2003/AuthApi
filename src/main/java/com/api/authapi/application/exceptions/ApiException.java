package com.api.authapi.application.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class ApiException extends RuntimeException {
    private final HttpStatus status;
    private final String code;

    public ApiException(HttpStatus status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }
}
