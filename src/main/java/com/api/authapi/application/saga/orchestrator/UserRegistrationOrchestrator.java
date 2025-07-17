package com.api.authapi.application.saga.orchestrator;

import com.api.authapi.application.saga.helpers.SagaErrorMapper;
import com.api.authapi.application.saga.services.UserRegistrationStateService;
import com.api.authapi.application.services.AuthService;
import com.api.authapi.application.services.UserService;
import com.api.authapi.domain.saga.command.ConfirmUserRegistrationCommand;
import com.api.authapi.domain.saga.reply.FailureUserRegistrationReply;
import com.api.authapi.domain.saga.reply.SuccessUserRegistrationReply;
import com.api.authapi.domain.saga.state.UserRegistrationState;
import com.api.authapi.domain.saga.step.UserRegistrationStep;
import com.api.authapi.domain.dto.auth.AuthResponse;
import com.api.authapi.domain.saga.command.RollbackUserRegistrationCommand;
import com.api.authapi.domain.saga.command.InitiateUserRegistrationCommand;
import com.api.authapi.infrastructure.messaging.publisher.UserRegistrationPublisher;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserRegistrationOrchestrator {

    private final AuthService authService;
    private final UserService userService;
    private final UserRegistrationStateService userRegistrationStateService;
    private final UserRegistrationPublisher publisher;

    public void handleInitiateUserRegistrationCommand(@Valid InitiateUserRegistrationCommand cmd) {
        UUID sagaId = cmd.sagaId();
        log.info("[UserRegistrationSagaOrchestrator::handleInitiateUserRegistrationCommand] Received saga start command. sagaId={}", sagaId);
        try {
            UserRegistrationState state = userRegistrationStateService.getOrStartSaga(sagaId);
            if (state.getStep() != UserRegistrationStep.STARTED) {
                log.debug("[UserRegistrationSagaOrchestrator::handleInitiateUserRegistrationCommand] Saga already progressed, skipping. sagaId={}", sagaId);
                return;
            }
            AuthResponse response = authService.register(cmd);
            userRegistrationStateService.markCreated(sagaId, response.getUser().getId());
            log.info("[UserRegistrationSagaOrchestrator::handleInitiateUserRegistrationCommand] User created. sagaId={}, userId={}", sagaId, response.getUser().getId());
            publishSuccessUserRegistrationReply(sagaId, response.getToken(), response.getRefreshToken());
        }
        catch (Exception ex) {
            log.warn("[UserRegistrationSagaOrchestrator::handleInitiateUserRegistrationCommand] Exception caught during user registration. sagaId={}", sagaId, ex);
            SagaErrorMapper.SagaError error = SagaErrorMapper.map(ex);
            publishFailureUserRegistrationReply(sagaId, error.code(), error.message());
            recoverCommand(sagaId);
        }
    }

    private void publishSuccessUserRegistrationReply(UUID sagaId,
                                                 String token,
                                                 String refreshToken) {
        log.info("[UserRegistrationSagaOrchestrator::publishSuccessUserRegistrationReply] Publishing success reply. sagaId={}", sagaId);
        publisher.publishSuccessUserRegistrationReply(
                SuccessUserRegistrationReply.builder()
                        .sagaId(sagaId)
                        .token(token)
                        .refreshToken(refreshToken)
                        .build());
    }

    private void publishFailureUserRegistrationReply(UUID sagaId,
                                                 Integer status,
                                                 String message) {
        log.warn("[UserRegistrationSagaOrchestrator::publishFailureUserRegistrationReply] Publishing failure reply. sagaId={}, status={}, message={}", sagaId, status, message);
        publisher.publishFailureUserRegistrationReply(
        FailureUserRegistrationReply.builder()
                .sagaId(sagaId)
                .status(status)
                .message(message)
                .build());
    }

    public void handleRollbackUserRegistrationCommand(@Valid RollbackUserRegistrationCommand cmd) {
        UUID sagaId = cmd.sagaId();
        log.info("[UserRegistrationSagaOrchestrator::handleRollbackUserRegistrationCommand] Compensating saga. sagaId={}", sagaId);
        try {
            UserRegistrationState state = userRegistrationStateService.getSagaState(sagaId);
            if (List.of(UserRegistrationStep.COMPENSATED, UserRegistrationStep.COMPLETED, UserRegistrationStep.FAILED)
                    .contains(state.getStep())) {
                log.debug("[UserRegistrationSagaOrchestrator::handleRollbackUserRegistrationCommand] Saga is completed or compensated. sagaId={}", sagaId);
                return;
            }
            userService.handleRollbackUserRegistration(state.getUserId());
            userRegistrationStateService.markCompensated(sagaId);
            log.info("[UserRegistrationSagaOrchestrator::handleRollbackUserRegistrationCommand] User rollback executed. sagaId={}", sagaId);
        } catch (Exception ex) {
            log.error("[UserRegistrationSagaOrchestrator::handleRollbackUserRegistrationCommand] Rollback failed. sagaId={}", sagaId, ex);
        }
    }

    public void handleConfirmUserRegistrationCommand(@Valid ConfirmUserRegistrationCommand cmd) {
        UUID sagaId = cmd.sagaId();
        log.info("[UserRegistrationSagaOrchestrator::handleConfirmUserRegistrationCommand] Confirming saga. sagaId={}", sagaId);
        try {
            UserRegistrationState state = userRegistrationStateService.getSagaState(sagaId);
            if (List.of(UserRegistrationStep.COMPENSATED, UserRegistrationStep.COMPLETED, UserRegistrationStep.FAILED)
                    .contains(state.getStep())) {
                log.debug("[UserRegistrationSagaOrchestrator::handleConfirmUserRegistrationCommand] Saga is already completed/compensated. sagaId={}", sagaId);
                return;
            }
            userRegistrationStateService.markCompleted(sagaId);
            log.info("[UserRegistrationSagaOrchestrator::handleConfirmUserRegistrationCommand] Saga marked as COMPLETED. sagaId={}", sagaId);
        } catch (Exception ex) {
            log.error("[UserRegistrationSagaOrchestrator::handleConfirmUserRegistrationCommand] Failed to confirm saga. sagaId={}", sagaId, ex);
        }
    }

    public void recoverCommand(UUID sagaId) {
        log.info("[UserRegistrationSagaOrchestrator::recoverCommand] Starting recovery for sagaId={}", sagaId);
        try {
            UserRegistrationState state = userRegistrationStateService.getSagaState(sagaId);
            if (state.getStep().equals(UserRegistrationStep.CREATED)) {
                log.info("[UserRegistrationSagaOrchestrator::recoverCommand] Saga was in CREATED. Executing rollback. sagaId={}", sagaId);
                userService.handleRollbackUserRegistration(state.getUserId());
                userRegistrationStateService.markCompensated(sagaId);
                log.info("[UserRegistrationSagaOrchestrator::recoverCommand] Saga marked as COMPENSATED. sagaId={}", sagaId);
            } else if (state.getStep().equals(UserRegistrationStep.STARTED)) {
                userRegistrationStateService.markFailed(sagaId);
                log.info("[UserRegistrationSagaOrchestrator::recoverCommand] Saga was in STARTED. Marking as FAILED. sagaId={}", sagaId);
            }
        } catch (Exception ex) {
            log.error("[UserRegistrationSagaOrchestrator::recoverCommand] Recovery failed for sagaId={}", sagaId, ex);
        }
    }
}
