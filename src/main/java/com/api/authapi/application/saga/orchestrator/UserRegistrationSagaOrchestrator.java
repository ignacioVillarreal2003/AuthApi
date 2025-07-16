package com.api.authapi.application.saga.orchestrator;

import com.api.authapi.application.saga.helpers.SagaErrorMapper;
import com.api.authapi.application.saga.services.UserRegistrationSagaStateService;
import com.api.authapi.application.services.AuthService;
import com.api.authapi.application.services.UserService;
import com.api.authapi.domain.saga.command.UserRegisterConfirmationCommand;
import com.api.authapi.domain.saga.reply.UserRegisterFailureReply;
import com.api.authapi.domain.saga.reply.UserRegisterSuccessReply;
import com.api.authapi.domain.saga.state.UserRegistrationSagaState;
import com.api.authapi.domain.saga.step.UserRegistrationSagaStep;
import com.api.authapi.domain.dto.auth.AuthResponse;
import com.api.authapi.domain.saga.command.UserRegisterCompensationCommand;
import com.api.authapi.domain.saga.command.UserRegisterInitialCommand;
import com.api.authapi.infrastructure.messaging.publisher.UserRegistrationPublisher;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserRegistrationSagaOrchestrator {

    private final AuthService authService;
    private final UserService userService;
    private final UserRegistrationSagaStateService sagaStateService;
    private final UserRegistrationPublisher replyPublisher;

    public void handleUserRegisterInitialCommand(@Valid UserRegisterInitialCommand cmd) {
        UUID sagaId = cmd.sagaId();
        log.info("[UserRegistrationSagaOrchestrator::handleUserRegisterInitialCommand] Received saga start command. sagaId={}", sagaId);
        if (sagaStateService.isStepCompleted(sagaId, UserRegistrationSagaStep.USER_CREATED)) {
            log.debug("[UserRegistrationSagaOrchestrator::handleUserRegisterInitialCommand] Step USER_CREATED already completed. sagaId={}", sagaId);
            return;
        }
        sagaStateService.startSaga(sagaId);
        try {
            AuthResponse response = authService.register(cmd);
            sagaStateService.markUserCreated(sagaId, response.getUser().getId());
            log.info("[UserRegistrationSagaOrchestrator::handleUserRegisterInitialCommand] User created. sagaId={}, userId={}", sagaId, response.getUser().getId());
            publishUserRegisterSuccessReply(sagaId, response.getUser().getEmail(), response.getToken(), response.getRefreshToken());
        }
        catch (Exception ex) {
            log.warn("[UserRegistrationSagaOrchestrator::handleUserRegisterInitialCommand] Exception caught during user registration. sagaId={}", sagaId, ex);
            SagaErrorMapper.SagaError error = SagaErrorMapper.map(ex);
            publishUserRegisterFailureReply(sagaId, error.code(), error.message());
        }
    }

    private void publishUserRegisterSuccessReply(UUID sagaId,
                                                 String email,
                                                 String token,
                                                 String refreshToken) {
        log.info("[UserRegistrationSagaOrchestrator::publishUserRegisterSuccessReply] Publishing success reply. sagaId={}", sagaId);
        replyPublisher.publishUserRegisterSuccessReply(
                UserRegisterSuccessReply.builder()
                        .sagaId(sagaId)
                        .email(email)
                        .token(token)
                        .refreshToken(refreshToken)
                        .build());
    }

    private void publishUserRegisterFailureReply(UUID sagaId,
                                                 Integer status,
                                                 String message) {
        log.warn("[UserRegistrationSagaOrchestrator::publishUserRegisterFailureReply] Publishing failure reply. sagaId={}, status={}, message={}", sagaId, status, message);
        replyPublisher.publishUserRegisterFailureReply(
        UserRegisterFailureReply.builder()
                .sagaId(sagaId)
                .status(status)
                .message(message)
                .build());
    }

    public void handleUserRegisterCompensationCommand(@Valid UserRegisterCompensationCommand cmd) {
        UUID sagaId = cmd.sagaId();
        log.info("[UserRegistrationSagaOrchestrator::handleUserRegisterCompensationCommand] Compensating saga. sagaId={}", sagaId);
        UserRegistrationSagaState state = sagaStateService.getUserRegistrationSagaState(sagaId);
        userService.deleteUserById(state.getUserId());
        sagaStateService.compensateSaga(sagaId);
    }

    public void handleUserRegisterConfirmationCommand(@Valid UserRegisterConfirmationCommand cmd) {
        UUID sagaId = cmd.sagaId();
        log.info("[UserRegistrationSagaOrchestrator::handleUserRegisterConfirmationCommand] Confirming saga. sagaId={}", sagaId);
        sagaStateService.completeSaga(sagaId);
    }
}
