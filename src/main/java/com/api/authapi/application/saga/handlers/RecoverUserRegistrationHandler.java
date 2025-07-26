package com.api.authapi.application.saga.handlers;

import com.api.authapi.application.saga.services.UserRegistrationStateService;
import com.api.authapi.application.services.auth.RegisterRollbackService;
import com.api.authapi.domain.saga.state.UserRegistrationState;
import com.api.authapi.domain.saga.step.UserRegistrationStep;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecoverUserRegistrationHandler {

    private final RegisterRollbackService registerRollbackService;
    private final UserRegistrationStateService userRegistrationStateService;

    public void handle(UUID sagaId) {
        try {
            UserRegistrationState state = userRegistrationStateService.getSagaState(sagaId);
            if (state.getStep().equals(UserRegistrationStep.CREATED)) {
                log.info("[UserRegistrationSagaOrchestrator::recoverCommand] Saga was in CREATED. Executing rollback. sagaId={}", sagaId);
                registerRollbackService.execute(state.getUserId());
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
