package dev.ignacio.villarreal.authenticationapi.application.exceptions.badRequest;

import dev.ignacio.villarreal.authenticationapi.application.exceptions.ApiException;
import org.springframework.http.HttpStatus;

public abstract class BadRequestException extends ApiException {
  public BadRequestException(String code, String message) {
    super(HttpStatus.BAD_REQUEST, code, message);
  }
}
