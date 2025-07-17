package com.api.authapi.infrastructure.messaging.listener;

import com.api.authapi.application.saga.orchestrator.UserRegistrationSagaOrchestrator;
import com.api.authapi.domain.saga.command.UserRegisterCompensationCommand;
import com.api.authapi.domain.saga.command.UserRegisterConfirmationCommand;
import com.api.authapi.domain.saga.command.UserRegisterInitialCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserRegistrationListener {

    private final UserRegistrationSagaOrchestrator orchestrator;

    @Retryable(
            retryFor = { AmqpException.class, TransientDataAccessException.class },
            maxAttempts = 5,
            backoff = @Backoff(delay = 1000, multiplier = 2.0)
    )
    @RabbitListener(queues = "${rabbit.queue.user-register-initial-command}")
    public void handleUserRegisterInitialCommand(@Valid @Payload UserRegisterInitialCommand cmd) {
        log.info("[UserRegistrationListener::handleUserRegisterInitialCommand] Received command sagaId={}", cmd.sagaId());
        orchestrator.handleUserRegisterInitialCommand(cmd);
        log.info("[UserRegistrationListener::handleUserRegisterInitialCommand] Processing finished sagaId={}", cmd.sagaId());
    }

    @Recover
    public void recoverUserRegisterInitialCommand(TransientDataAccessException ex, UserRegisterInitialCommand cmd) {
        log.error("[Recover] Initial registration permanently failed. sagaId={}", cmd.sagaId(), ex);
        orchestrator.recoverCommand(cmd.sagaId());
    }

    @Retryable(
            retryFor = { AmqpException.class, TransientDataAccessException.class },
            maxAttempts = 5,
            backoff = @Backoff(delay = 1000, multiplier = 2.0)
    )
    @RabbitListener(queues = "${rabbit.queue.user-register-compensation-command}")
    public void handleUserRegisterCompensationCommand(@Valid @Payload UserRegisterCompensationCommand cmd) {
        log.info("[UserRegistrationListener::handleUserRegisterCompensationCommand] Received command sagaId={}", cmd.sagaId());
        orchestrator.handleUserRegisterCompensationCommand(cmd);
        log.info("[UserRegistrationListener::handleUserRegisterCompensationCommand] Processing finished sagaId={}", cmd.sagaId());

    }

    @Recover
    public void recoverUserRegisterCompensationCommand(TransientDataAccessException ex, UserRegisterCompensationCommand cmd) {
        log.error("[Recover] Confirmation permanently failed. sagaId={}", cmd.sagaId(), ex);
        orchestrator.recoverCommand(cmd.sagaId());
    }

    @Retryable(
            retryFor = { AmqpException.class, TransientDataAccessException.class },
            maxAttempts = 5,
            backoff = @Backoff(delay = 1000, multiplier = 2.0)
    )
    @RabbitListener(queues = "${rabbit.queue.user-register-confirmation-command}")
    public void handleUserRegisterConfirmationCommand(@Valid @Payload UserRegisterConfirmationCommand cmd) {
        log.info("[UserRegistrationListener::handleUserRegisterConfirmationCommand] Received command sagaId={}", cmd.sagaId());
        orchestrator.handleUserRegisterConfirmationCommand(cmd);
        log.info("[UserRegistrationListener::handleUserRegisterConfirmationCommand] Processing finished sagaId={}", cmd.sagaId());
    }

    @Recover
    public void recoverUserRegisterConfirmationCommand(TransientDataAccessException ex, UserRegisterConfirmationCommand cmd) {
        log.error("[Recover] Compensation permanently failed after retries. sagaId={}", cmd.sagaId(), ex);
        orchestrator.recoverCommand(cmd.sagaId());
    }
}
