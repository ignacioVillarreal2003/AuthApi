package com.api.authapi.infrastructure.messaging.listener;

import com.api.authapi.application.saga.orchestrator.UserRegistrationOrchestrator;
import com.api.authapi.domain.saga.command.RollbackUserRegistrationCommand;
import com.api.authapi.domain.saga.command.ConfirmUserRegistrationCommand;
import com.api.authapi.domain.saga.command.InitiateUserRegistrationCommand;
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

    private final UserRegistrationOrchestrator orchestrator;

    @Retryable(
            retryFor = { AmqpException.class, TransientDataAccessException.class },
            maxAttempts = 5,
            backoff = @Backoff(delay = 1000, multiplier = 2.0)
    )
    @RabbitListener(queues = "${rabbit.queue.initiate-user-registration-command}")
    public void handleInitiateUserRegistrationCommand(@Valid @Payload InitiateUserRegistrationCommand cmd) {
        orchestrator.handleInitiateUserRegistrationCommand(cmd);
    }

    @Recover
    public void recoverInitiateUserRegistrationCommand(TransientDataAccessException ex, InitiateUserRegistrationCommand cmd) {
        orchestrator.recoverCommand(cmd.sagaId());
    }

    @Retryable(
            retryFor = { AmqpException.class, TransientDataAccessException.class },
            maxAttempts = 5,
            backoff = @Backoff(delay = 1000, multiplier = 2.0)
    )
    @RabbitListener(queues = "${rabbit.queue.rollback-user-registration-command}")
    public void handleRollbackUserRegistrationCommand(@Valid @Payload RollbackUserRegistrationCommand cmd) {
        orchestrator.handleRollbackUserRegistrationCommand(cmd);
    }

    @Recover
    public void recoverRollbackUserRegistrationCommand(TransientDataAccessException ex, RollbackUserRegistrationCommand cmd) {
        orchestrator.recoverCommand(cmd.sagaId());
    }

    @Retryable(
            retryFor = { AmqpException.class, TransientDataAccessException.class },
            maxAttempts = 5,
            backoff = @Backoff(delay = 1000, multiplier = 2.0)
    )
    @RabbitListener(queues = "${rabbit.queue.confirm-user-registration-command}")
    public void handleConfirmUserRegistrationCommand(@Valid @Payload ConfirmUserRegistrationCommand cmd) {
        orchestrator.handleConfirmUserRegistrationCommand(cmd);
    }

    @Recover
    public void recoverConfirmUserRegistrationCommand(TransientDataAccessException ex, ConfirmUserRegistrationCommand cmd) {
        orchestrator.recoverCommand(cmd.sagaId());
    }
}
