package com.api.authapi.api.consumers;

import com.api.authapi.api.producers.UserRegisterSagaPublisher;
import com.api.authapi.application.services.AuthService;
import com.api.authapi.application.services.UserRegisterSagaService;
import com.api.authapi.application.services.UserService;
import com.api.authapi.domain.constants.SagaStep;
import com.api.authapi.domain.dtos.auth.AuthResponse;
import com.api.authapi.domain.dtos.auth.CompensateUserRegisterCommand;
import com.api.authapi.domain.dtos.auth.UserRegisterCommand;
import com.api.authapi.domain.dtos.auth.UserRegisterReply;
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

    private final AuthService authService;
    private final UserService userService;
    private final UserRegisterSagaPublisher userRegisterSagaPublisher;
    private final UserRegisterSagaService registerSagaService;

    @RabbitListener(queues = "${rabbit.queue.user-register-command}",
            containerFactory = "rabbitListenerContainerFactory")
    public void handleUserRegisterCommand(@Valid @Payload UserRegisterCommand message) {
        UUID sagaId = message.sagaId();

        registerSagaService.startSaga(sagaId);

        if (registerSagaService.isStepCompleted(sagaId, SagaStep.USER_CREATED)) {
            return;
        }

        try {
            AuthResponse authResponse = authService.register(message);

            registerSagaService.markUserCreated(sagaId, authResponse.getUser().getId());

            userRegisterSagaPublisher.publishUserRegisterReply(
                    UserRegisterReply.builder()
                        .sagaId(sagaId)
                        .success(true)
                        .token(authResponse.getToken())
                        .refreshToken(authResponse.getRefreshToken())
                        .build()
            );

            registerSagaService.completeSaga(sagaId);
        }
        catch (ResponseStatusException ex) {
            publishUserRegisterReplyError(sagaId, "[" + ex.getStatusCode().value() + "] " + ex.getReason());
        }
        catch (Exception ex) {
            if (ex.getCause() instanceof MethodArgumentNotValidException validationEx) {
                StringBuilder errors = new StringBuilder();
                validationEx.getBindingResult().getFieldErrors().forEach(fe ->
                        errors.append(fe.getField()).append(": ").append(fe.getDefaultMessage()).append("; ")
                );
                publishUserRegisterReplyError(sagaId, "[400] " + errors);
            }
            else {
                publishUserRegisterReplyError(sagaId, "[500] " + ex.getMessage());
            }
        }
    }

    private void publishUserRegisterReplyError(UUID sagaId, String error) {
        UserRegisterReply response = UserRegisterReply.builder()
                .sagaId(sagaId)
                .success(false)
                .errorMessage(error)
                .build();
        userRegisterSagaPublisher.publishUserRegisterReply(response);
    }

    @RabbitListener(queues = "${rabbit.queue.compensate-user-register-command}",
            containerFactory = "rabbitListenerContainerFactory")
    public void handleCompensateRegisterUserCommand(@Valid @Payload CompensateUserRegisterCommand message) {
        UUID sagaId = message.sagaId();

        registerSagaService.getUserId(sagaId).ifPresent(userService::deleteUserById);
        registerSagaService.compensateSaga(sagaId);
    }
}
