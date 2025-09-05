package dev.ignacio.villarreal.authenticationapi.application.handlers;

import dev.ignacio.villarreal.authenticationapi.application.exceptions.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
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

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Object> handleApi(ApiException ex, HttpServletRequest req) {
        return buildError(ex.getStatus(), ex.getMessage(), req.getRequestURI());
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
