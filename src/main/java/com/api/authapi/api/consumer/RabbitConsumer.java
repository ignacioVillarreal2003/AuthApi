package com.api.authapi.api.consumer;

import com.api.authapi.api.producer.RabbitProducer;
import com.api.authapi.application.services.UserService;
import com.api.authapi.domain.dtos.user.AuthResponse;
import com.api.authapi.domain.dtos.user.RegisterCompensateRequest;
import com.api.authapi.domain.dtos.user.RegisterRequest;
import com.api.authapi.domain.dtos.user.RegisterResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Validated
public class RabbitConsumer {

    private final UserService authService;
    private final RabbitProducer rabbitProducer;

    @RabbitListener(queues = "${rabbit.queue.queue-registration-request}")
    public void receiveMessageRegistrationRequest(@Valid @Payload RegisterRequest message) {
        UUID sagaId = message.sagaId();
        try {
            AuthResponse authResponse = authService.register(message);

            RegisterResponse response = RegisterResponse.builder()
                    .sagaId(sagaId)
                    .userId(authResponse.getUser().getId())
                    .success(true)
                    .token(authResponse.getToken())
                    .refreshToken(authResponse.getRefreshToken())
                    .build();

            rabbitProducer.sendMessageRegistrationResponse(response);
        }
        catch (ResponseStatusException ex) {
            RegisterResponse response = RegisterResponse.builder()
                    .sagaId(sagaId)
                    .success(false)
                    .errorMessage("[" + ex.getStatusCode().value() + "] " + ex.getReason())
                    .build();
            rabbitProducer.sendMessageRegistrationResponse(response);
        }
        catch (Exception ex) {
            if (ex.getCause() instanceof MethodArgumentNotValidException validationEx) {
                StringBuilder errors = new StringBuilder();
                validationEx.getBindingResult().getFieldErrors().forEach(fe ->
                        errors.append(fe.getField()).append(": ").append(fe.getDefaultMessage()).append("; ")
                );
                RegisterResponse response = RegisterResponse.builder()
                        .sagaId(sagaId)
                        .success(false)
                        .errorMessage("[400] " + errors)
                        .build();
                rabbitProducer.sendMessageRegistrationResponse(response);
            }
            else {
                RegisterResponse response = RegisterResponse.builder()
                        .sagaId(sagaId)
                        .success(false)
                        .errorMessage("[500] " + ex.getMessage())
                        .build();
                rabbitProducer.sendMessageRegistrationResponse(response);
            }
        }
    }

    @RabbitListener(queues = "${rabbit.queue.queue-registration-rollback}")
    public void receiveMessageRegistrationRollback(@Valid @Payload RegisterCompensateRequest message) {
        Long userId = message.userId();
        authService.deleteUserPermanently(userId);
    }
}
