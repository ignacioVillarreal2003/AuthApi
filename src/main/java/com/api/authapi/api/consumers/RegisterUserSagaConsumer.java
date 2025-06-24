package com.api.authapi.api.consumers;

import com.api.authapi.api.producers.RegisterUserSagaPublisher;
import com.api.authapi.application.services.UserService;
import com.api.authapi.domain.dtos.user.AuthResponse;
import com.api.authapi.domain.dtos.user.CompensateRegisterUserCommand;
import com.api.authapi.domain.dtos.user.RegisterUserCommand;
import com.api.authapi.domain.dtos.user.RegisterUserReply;
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
public class RegisterUserSagaConsumer {

    private final UserService authService;
    private final RegisterUserSagaPublisher registrationPublisher;

    @RabbitListener(queues = "${rabbit.queue.user-register-command}")
    public void handleRegisterUserCommand(@Valid @Payload RegisterUserCommand message) {
        UUID sagaId = message.sagaId();
        try {
            AuthResponse authResponse = authService.register(message);

            RegisterUserReply response = RegisterUserReply.builder()
                    .sagaId(sagaId)
                    .userId(authResponse.getUser().getId())
                    .success(true)
                    .token(authResponse.getToken())
                    .refreshToken(authResponse.getRefreshToken())
                    .build();

            registrationPublisher.publishRegisterUserReply(response);
        }
        catch (ResponseStatusException ex) {
            RegisterUserReply response = RegisterUserReply.builder()
                    .sagaId(sagaId)
                    .success(false)
                    .errorMessage("[" + ex.getStatusCode().value() + "] " + ex.getReason())
                    .build();
            registrationPublisher.publishRegisterUserReply(response);
        }
        catch (Exception ex) {
            if (ex.getCause() instanceof MethodArgumentNotValidException validationEx) {
                StringBuilder errors = new StringBuilder();
                validationEx.getBindingResult().getFieldErrors().forEach(fe ->
                        errors.append(fe.getField()).append(": ").append(fe.getDefaultMessage()).append("; ")
                );
                RegisterUserReply response = RegisterUserReply.builder()
                        .sagaId(sagaId)
                        .success(false)
                        .errorMessage("[400] " + errors)
                        .build();
                registrationPublisher.publishRegisterUserReply(response);
            }
            else {
                RegisterUserReply response = RegisterUserReply.builder()
                        .sagaId(sagaId)
                        .success(false)
                        .errorMessage("[500] " + ex.getMessage())
                        .build();
                registrationPublisher.publishRegisterUserReply(response);
            }
        }
    }

    @RabbitListener(queues = "${rabbit.queue.compensate-user-register-command}")
    public void handleCompensateRegisterUserCommand(@Valid @Payload CompensateRegisterUserCommand message) {
        Long userId = message.userId();
        authService.deleteUserPermanently(userId);
    }
}
