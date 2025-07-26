package com.api.authapi.application.exceptions;

import org.springframework.http.HttpStatus;

public abstract class BadRequestException extends ApiException {
  public BadRequestException(String message, String code) {
    super(message, HttpStatus.BAD_REQUEST, code);
  }
}
