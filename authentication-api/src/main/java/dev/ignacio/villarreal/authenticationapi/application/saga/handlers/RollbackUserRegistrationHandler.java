package dev.ignacio.villarreal.authenticationapi.application.saga.handlers;

import dev.ignacio.villarreal.authenticationapi.application.saga.services.UserRegistrationStateService;
import dev.ignacio.villarreal.authenticationapi.application.services.user.UserDeletionService;
import dev.ignacio.villarreal.authenticationapi.application.services.userRole.UserRoleRevocationService;
import dev.ignacio.villarreal.authenticationapi.domain.saga.command.RollbackUserRegistrationCommand;
import dev.ignacio.villarreal.authenticationapi.domain.saga.state.UserRegistrationState;
import dev.ignacio.villarreal.authenticationapi.domain.saga.step.UserRegistrationStep;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RollbackUserRegistrationHandler {

    private final UserRegistrationStateService userRegistrationStateService;
    private final UserDeletionService userDeletionService;
    private final UserRoleRevocationService userRoleDeallocationService;

    @Transactional
    public void rollbackUserRegistration(@Valid RollbackUserRegistrationCommand cmd) {
        UUID sagaId = cmd.sagaId();
        try {
            UserRegistrationState state = userRegistrationStateService.getSagaState(sagaId);
            if (List.of(UserRegistrationStep.COMPENSATED, UserRegistrationStep.COMPLETED, UserRegistrationStep.FAILED)
                    .contains(state.getStep())) {
                return;
            }
            if (state.isNewUser()) {
                userDeletionService.deleteById(state.getUserId());
            }
            else {
                state.getRoles().forEach(role -> {
                    userRoleDeallocationService.removeRoleFromUser(state.getUserId(), role);
                });
            }
            userRegistrationStateService.markCompensated(sagaId);
            log.info("[UserRegistrationSagaOrchestrator::handleRollbackUserRegistrationCommand] User rollback executed. sagaId={}", sagaId);
        } catch (Exception ex) {
            log.error("[UserRegistrationSagaOrchestrator::handleRollbackUserRegistrationCommand] Rollback failed. sagaId={}", sagaId, ex);
        }
    }
}
