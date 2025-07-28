package com.api.authapi.application.saga.handlers;

import com.api.authapi.application.saga.services.UserRegistrationStateService;
import com.api.authapi.application.services.user.UserDeletionService;
import com.api.authapi.application.services.userRole.UserRoleRevocationService;
import com.api.authapi.domain.saga.state.UserRegistrationState;
import com.api.authapi.domain.saga.step.UserRegistrationStep;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecoverUserRegistrationHandler {

    private final UserRegistrationStateService userRegistrationStateService;
    private final UserDeletionService userDeletionService;
    private final UserRoleRevocationService userRoleDeallocationService;

    public void recoverUserRegistration(UUID sagaId) {
        try {
            UserRegistrationState state = userRegistrationStateService.getSagaState(sagaId);
            if (List.of(UserRegistrationStep.USER_CREATED, UserRegistrationStep.PENDING_VERIFICATION)
                    .contains(state.getStep())) {
                if (state.isNewUser()) {
                    userDeletionService.deleteById(state.getUserId());
                }
                else {
                    state.getRoles().forEach(role -> {
                        userRoleDeallocationService.removeRoleFromUser(state.getUserId(), role);
                    });
                }
                userRegistrationStateService.markCompensated(sagaId);
            }
            else if (state.getStep().equals(UserRegistrationStep.STARTED)) {
                userRegistrationStateService.markFailed(sagaId);
            }
        } catch (Exception ex) {
            log.error("[UserRegistrationSagaOrchestrator::recoverCommand] Recovery failed for sagaId={}", sagaId, ex);
        }
    }
}
