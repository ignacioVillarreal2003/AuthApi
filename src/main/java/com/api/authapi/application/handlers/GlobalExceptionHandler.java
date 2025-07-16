package com.api.authapi.application.handlers;

import com.api.authapi.application.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Object> handleResponseStatus(ResponseStatusException ex) {
        log.warn("[GlobalExceptionHandler::handleResponseStatus] ResponseStatusException - {}: {}", ex.getStatusCode(), ex.getReason());
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDate.now().toString());
        body.put("status", ex.getStatusCode().value());
        body.put("error", ex.getReason());
        return new ResponseEntity<>(body, ex.getStatusCode());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        log.warn("[GlobalExceptionHandler::handleMethodArgumentNotValid] Validation failed: {}", ex.getMessage());
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDate.now().toString());
        body.put("status", ex.getStatusCode().value());
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            log.warn("[ValidationError] {}: {}", fieldName, errorMessage);
            body.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(body, ex.getStatusCode());
    }

    @ExceptionHandler({InvalidCredentialsException.class, InvalidRefreshTokenException.class, InvalidRoleException.class})
    public ResponseEntity<Object> handleUnauthorized(RuntimeException ex, HttpServletRequest req) {
        log.warn("[GlobalExceptionHandler::handleUnauthorized] Unauthorized - URI: {} - {}", req.getRequestURI(), ex.getMessage());
        return buildError(HttpStatus.UNAUTHORIZED, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler({UserNotFoundException.class, RoleNotFoundException.class})
    public ResponseEntity<Object> handleNotFound(RuntimeException ex, HttpServletRequest req) {
        log.warn("[GlobalExceptionHandler::handleNotFound] Not Found - URI: {} - {}", req.getRequestURI(), ex.getMessage());
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAll(Exception ex, HttpServletRequest req) {
        log.error("[GlobalExceptionHandler::handleAll] Internal error - URI: {} - {}", req.getRequestURI(), ex.getMessage(), ex);
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", req.getRequestURI());
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<Object> handleDisabled(DisabledException ex, HttpServletRequest req) {
        log.warn("[GlobalExceptionHandler::handleDisabled] Account disabled - URI: {} - {}", req.getRequestURI(), ex.getMessage());
        return buildError(HttpStatus.FORBIDDEN, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<Object> handleLocked(LockedException ex, HttpServletRequest req) {
        log.warn("[GlobalExceptionHandler::handleLocked] Account locked - URI: {} - {}", req.getRequestURI(), ex.getMessage());
        return buildError(HttpStatus.LOCKED, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(CustomAccountExpiredException.class)
    public ResponseEntity<Object> handleAccountExpired(CustomAccountExpiredException ex, HttpServletRequest req) {
        log.warn("[GlobalExceptionHandler::handleAccountExpired] Account expired - URI: {} - {}", req.getRequestURI(), ex.getMessage());
        return buildError(HttpStatus.FORBIDDEN, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(CredentialsExpiredException.class)
    public ResponseEntity<Object> handleCredentialsExpired(CredentialsExpiredException ex, HttpServletRequest req) {
        log.warn("[GlobalExceptionHandler::handleCredentialsExpired] Credentials expired - URI: {} - {}", req.getRequestURI(), ex.getMessage());
        return buildError(HttpStatus.FORBIDDEN, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(UserIsAdministratorException.class)
    public ResponseEntity<Object> handleUserIsAdministrator(UserIsAdministratorException ex, HttpServletRequest req) {
        log.warn("[GlobalExceptionHandler::handleUserIsAdministrator] Admin user restriction - URI: {} - {}", req.getRequestURI(), ex.getMessage());
        return buildError(HttpStatus.CONFLICT, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(RoleAlreadyAssignedException.class)
    public ResponseEntity<Object> handleRoleAlreadyAssigned(RoleAlreadyAssignedException ex, HttpServletRequest req) {
        log.warn("[GlobalExceptionHandler::handleRoleAlreadyAssigned] Role conflict - URI: {} - {}", req.getRequestURI(), ex.getMessage());
        return buildError(HttpStatus.CONFLICT, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(CustomAuthenticationException.class)
    public ResponseEntity<Object> handleCustomAuthentication(CustomAuthenticationException ex, HttpServletRequest req) {
        log.warn("[GlobalExceptionHandler::handleCustomAuthentication] Custom authentication error - URI: {} - {}", req.getRequestURI(), ex.getMessage());
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), req.getRequestURI());
    }

    private ResponseEntity<Object> buildError(HttpStatus status, String message, String path) {
        log.debug("[GlobalExceptionHandler::buildError] status={}, message={}, path={}", status, message, path);
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDate.now().toString());
        body.put("status", status.value());
        body.put("error", message);
        body.put("path", path);
        return ResponseEntity.status(status).body(body);
    }
}
