package com.api.authapi.application.exceptions.unauthorized;

import com.api.authapi.application.exceptions.ApiException;
import org.springframework.http.HttpStatus;

public abstract class UnauthorizedException extends ApiException {
  public UnauthorizedException(String message, String code) {
    super(message, HttpStatus.UNAUTHORIZED, code);
  }
}
