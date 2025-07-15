package com.api.authapi.application.saga.orchestrator;

import com.api.authapi.application.saga.helpers.SagaErrorMapper;
import com.api.authapi.application.saga.services.UserRegistrationSagaStateService;
import com.api.authapi.application.services.AuthService;
import com.api.authapi.application.services.UserService;
import com.api.authapi.domain.saga.command.UserRegisterConfirmationCommand;
import com.api.authapi.domain.saga.state.UserRegistrationSagaState;
import com.api.authapi.domain.saga.step.UserRegistrationSagaStep;
import com.api.authapi.domain.dto.auth.AuthResponse;
import com.api.authapi.domain.saga.command.UserRegisterCompensationCommand;
import com.api.authapi.domain.saga.command.UserRegisterInitialCommand;
import com.api.authapi.infrastructure.messaging.publisher.UserRegistrationPublisher;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserRegistrationSagaOrchestrator {

    private final AuthService authService;
    private final UserService userService;
    private final UserRegistrationSagaStateService sagaStateService;
    private final UserRegistrationPublisher replyPublisher;

    public void handleUserRegisterCommand(@Valid UserRegisterInitialCommand cmd) {
        UUID sagaId = cmd.sagaId();
        if (sagaStateService.isStepCompleted(sagaId, UserRegistrationSagaStep.USER_CREATED)) {
            return;
        }
        sagaStateService.startSaga(sagaId);
        try {
            AuthResponse response = authService.register(cmd);
            sagaStateService.markUserCreated(sagaId, response.getUser().getId());
            publishUserRegisterReplySuccess(sagaId, response.getUser().getEmail(), response.getToken(), response.getRefreshToken());
            sagaStateService.completeSaga(sagaId);
        }
        catch (Exception ex) {
            SagaErrorMapper.SagaError error = SagaErrorMapper.map(ex);
            publishUserRegisterReplyFailure(sagaId, error.code(), error.message());
        }
    }

    private void publishUserRegisterReplySuccess(UUID sagaId,
                                                 String email,
                                                 String token,
                                                 String refreshToken) {
        replyPublisher.publishUserRegisterReply(
                UserRegisterReply.builder()
                        .sagaId(sagaId)
                        .success(true)
                        .email(email)
                        .token(token)
                        .refreshToken(refreshToken)
                        .build());
    }

    private void publishUserRegisterReplyFailure(UUID sagaId,
                                                 int status,
                                                 String reason) {
        replyPublisher.publishUserRegisterReply(
                UserRegisterReply.builder()
                        .sagaId(sagaId)
                        .success(false)
                        .errorMessage("[" + status + "] " + reason)
                        .build());
    }

    public void handleCompensateRegisterCommand(@Valid UserRegisterCompensationCommand cmd) {
        UUID sagaId = cmd.sagaId();
        UserRegistrationSagaState state = sagaStateService.getUserRegistrationSagaState(sagaId);
        userService.deleteUserById(state.getUserId());
        sagaStateService.compensateSaga(sagaId);
    }

    public void handleUserRegisterInitialCommand(@Valid UserRegisterInitialCommand cmd) {
    }

    public void handleUserRegisterCompensationCommand(@Valid UserRegisterCompensationCommand cmd) {
    }

    public void handleUserRegisterConfirmationCommand(@Valid UserRegisterConfirmationCommand cmd) {

    }
}
