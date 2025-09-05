package dev.ignacio.villarreal.authenticationapi.application.saga.helpers;

import dev.ignacio.villarreal.authenticationapi.application.exceptions.notFound.SagaNotFoundException;
import dev.ignacio.villarreal.authenticationapi.application.exceptions.unauthorized.InvalidCredentialsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;

@Slf4j
public class SagaErrorMapper {

    public record SagaError(Integer code, String message) {}

    public static SagaError map(Throwable ex) {
        log.warn("[SagaErrorMapper::map] Mapping exception: {}", ex.toString());
        if (ex instanceof InvalidCredentialsException) {
            return new SagaError(401, ex.getMessage());
        }
        if (ex instanceof SagaNotFoundException) {
            return new SagaError(404, ex.getMessage());
        }
        if (ex instanceof ResponseStatusException rse) {
            return new SagaError(rse.getStatusCode().value(), rse.getReason());
        }
        if (ex.getCause() instanceof MethodArgumentNotValidException validationEx) {
            String errors = validationEx.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                    .collect(Collectors.joining("; "));
            return new SagaError(400, errors);
        }
        return new SagaError(500, ex.getMessage());
    }
}
