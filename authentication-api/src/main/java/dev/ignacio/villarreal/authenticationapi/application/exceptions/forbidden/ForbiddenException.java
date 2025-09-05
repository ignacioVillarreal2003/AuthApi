package dev.ignacio.villarreal.authenticationapi.application.exceptions.forbidden;

import dev.ignacio.villarreal.authenticationapi.application.exceptions.ApiException;
import org.springframework.http.HttpStatus;

public abstract class ForbiddenException extends ApiException {
    public ForbiddenException(String message, String code) {
        super(HttpStatus.FORBIDDEN, code, message);
    }
}
