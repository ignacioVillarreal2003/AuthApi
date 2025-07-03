package com.api.authapi.api.consumers;

import com.api.authapi.api.producers.UserRegisterSagaPublisher;
import com.api.authapi.application.services.UserService;
import com.api.authapi.domain.dtos.user.AuthResponse;
import com.api.authapi.domain.dtos.user.CompensaterUserRegisteCommand;
import com.api.authapi.domain.dtos.user.UserRegisterCommand;
import com.api.authapi.domain.dtos.user.UserRegisterReply;

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
public class UserRegisterSagaConsumer {

    private final UserService authService;
    private final UserRegisterSagaPublisher userRegisterSagaPublisher;

    @RabbitListener(queues = "${rabbit.queue.user-register-command}")
    public void handleUserRegisterCommand(@Valid @Payload UserRegisterCommand message) {
        UUID sagaId = message.sagaId();
        try {
            AuthResponse authResponse = authService.register(message);

            UserRegisterReply response = UserRegisterReply.builder()
                    .sagaId(sagaId)
                    .userId(authResponse.getUser().getId())
                    .success(true)
                    .token(authResponse.getToken())
                    .refreshToken(authResponse.getRefreshToken())
                    .build();

            userRegisterSagaPublisher.publishUserRegisterReply(response);
        }
        catch (ResponseStatusException ex) {
            UserRegisterReply response = UserRegisterReply.builder()
                    .sagaId(sagaId)
                    .success(false)
                    .errorMessage("[" + ex.getStatusCode().value() + "] " + ex.getReason())
                    .build();
            userRegisterSagaPublisher.publishUserRegisterReply(response);
        }
        catch (Exception ex) {
            if (ex.getCause() instanceof MethodArgumentNotValidException validationEx) {
                StringBuilder errors = new StringBuilder();
                validationEx.getBindingResult().getFieldErrors().forEach(fe ->
                        errors.append(fe.getField()).append(": ").append(fe.getDefaultMessage()).append("; ")
                );
                UserRegisterReply response = UserRegisterReply.builder()
                        .sagaId(sagaId)
                        .success(false)
                        .errorMessage("[400] " + errors)
                        .build();
                userRegisterSagaPublisher.publishUserRegisterReply(response);
            }
            else {
                UserRegisterReply response = UserRegisterReply.builder()
                        .sagaId(sagaId)
                        .success(false)
                        .errorMessage("[500] " + ex.getMessage())
                        .build();
                userRegisterSagaPublisher.publishUserRegisterReply(response);
            }
        }
    }

    @RabbitListener(queues = "${rabbit.queue.compensate-user-register-command}")
    public void handleCompensateRegisterUserCommand(@Valid @Payload CompensaterUserRegisteCommand message) {
        Long userId = message.userId();
        authService.deleteUserPermanently(userId);
    }
}
