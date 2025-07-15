package com.api.authapi.infrastructure.messaging.listener;

import com.api.authapi.application.saga.orchestrator.UserRegistrationSagaOrchestrator;
import com.api.authapi.domain.saga.command.UserRegisterCompensationCommand;
import com.api.authapi.domain.saga.command.UserRegisterConfirmationCommand;
import com.api.authapi.domain.saga.command.UserRegisterInitialCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRegistrationListener {

    private final UserRegistrationSagaOrchestrator orchestrator;

    @Retryable(
            retryFor = { Exception.class },
            maxAttempts = 5,
            backoff = @Backoff(delay = 1000, multiplier = 2.0)
    )
    @RabbitListener(queues = "${rabbit.queue.user-register-initial-command}")
    public void handleUserRegisterInitialCommand(@Valid @Payload UserRegisterInitialCommand cmd) {
        orchestrator.handleUserRegisterInitialCommand(cmd);
    }

    @Retryable(
            retryFor = { Exception.class },
            maxAttempts = 5,
            backoff = @Backoff(delay = 1000, multiplier = 2.0)
    )
    @RabbitListener(queues = "${rabbit.queue.user-register-compensation-command}")
    public void handleUserRegisterCompensationCommand(@Valid @Payload UserRegisterCompensationCommand cmd) {
        orchestrator.handleUserRegisterCompensationCommand(cmd);
    }

    @Retryable(
            retryFor = { Exception.class },
            maxAttempts = 5,
            backoff = @Backoff(delay = 1000, multiplier = 2.0)
    )
    @RabbitListener(queues = "${rabbit.queue.user-register-confirmation-command}")
    public void handleUserRegisterConfirmationCommand(@Valid @Payload UserRegisterConfirmationCommand cmd) {
        orchestrator.handleUserRegisterConfirmationCommand(cmd);
    }
}
