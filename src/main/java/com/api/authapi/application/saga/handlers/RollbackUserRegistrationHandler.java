package com.api.authapi.application.saga.handlers;

import com.api.authapi.application.saga.services.UserRegistrationStateService;
import com.api.authapi.application.services.auth.RegisterRollbackService;
import com.api.authapi.domain.saga.command.RollbackUserRegistrationCommand;
import com.api.authapi.domain.saga.state.UserRegistrationState;
import com.api.authapi.domain.saga.step.UserRegistrationStep;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RollbackUserRegistrationHandler {

    private final RegisterRollbackService registerRollbackService;
    private final UserRegistrationStateService userRegistrationStateService;

    public void handle(@Valid RollbackUserRegistrationCommand cmd) {
        UUID sagaId = cmd.sagaId();
        log.info("[UserRegistrationSagaOrchestrator::handleRollbackUserRegistrationCommand] Compensating saga. sagaId={}", sagaId);
        try {
            UserRegistrationState state = userRegistrationStateService.getSagaState(sagaId);
            if (List.of(UserRegistrationStep.COMPENSATED, UserRegistrationStep.COMPLETED, UserRegistrationStep.FAILED)
                    .contains(state.getStep())) {
                log.debug("[UserRegistrationSagaOrchestrator::handleRollbackUserRegistrationCommand] Saga is completed or compensated. sagaId={}", sagaId);
                return;
            }
            registerRollbackService.execute(state.getUserId());
            userRegistrationStateService.markCompensated(sagaId);
            log.info("[UserRegistrationSagaOrchestrator::handleRollbackUserRegistrationCommand] User rollback executed. sagaId={}", sagaId);
        } catch (Exception ex) {
            log.error("[UserRegistrationSagaOrchestrator::handleRollbackUserRegistrationCommand] Rollback failed. sagaId={}", sagaId, ex);
        }
    }
}
