package com.api.authapi.application.saga.handlers;

import com.api.authapi.application.saga.services.UserRegistrationStateService;
import com.api.authapi.domain.saga.command.ConfirmUserRegistrationCommand;
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
public class ConfirmUserRegistrationHandler {

    private final UserRegistrationStateService userRegistrationStateService;

    public void confirmUserRegistration(@Valid ConfirmUserRegistrationCommand cmd) {
        UUID sagaId = cmd.sagaId();
        try {
            UserRegistrationState state = userRegistrationStateService.getSagaState(sagaId);
            if (List.of(UserRegistrationStep.COMPENSATED, UserRegistrationStep.COMPLETED, UserRegistrationStep.FAILED)
                    .contains(state.getStep())) {
                return;
            }
            userRegistrationStateService.markCompleted(sagaId);
        } catch (Exception ex) {
            log.error("[UserRegistrationSagaOrchestrator::handleConfirmUserRegistrationCommand] Failed to confirm saga. sagaId={}", sagaId, ex);
        }
    }
}
