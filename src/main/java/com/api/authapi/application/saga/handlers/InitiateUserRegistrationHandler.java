package com.api.authapi.application.saga.handlers;

import com.api.authapi.application.saga.helpers.SagaErrorMapper;
import com.api.authapi.application.saga.services.UserRegistrationStateService;
import com.api.authapi.application.services.authentication.RegisterService;
import com.api.authapi.domain.dto.auth.RegisterResponse;
import com.api.authapi.domain.saga.command.InitiateUserRegistrationCommand;
import com.api.authapi.domain.saga.reply.AwaitingVerificationUserRegistrationReply;
import com.api.authapi.domain.saga.reply.FailureUserRegistrationReply;
import com.api.authapi.domain.saga.reply.SuccessUserRegistrationReply;
import com.api.authapi.domain.saga.state.UserRegistrationState;
import com.api.authapi.domain.saga.step.UserRegistrationStep;
import com.api.authapi.infrastructure.messaging.publisher.UserRegistrationPublisher;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InitiateUserRegistrationHandler {

    private final RegisterService registerService;
    private final UserRegistrationStateService userRegistrationStateService;
    private final UserRegistrationPublisher publisher;
    private final RecoverUserRegistrationHandler recoverUserRegistrationService;

    @Transactional
    public void handle(@Valid InitiateUserRegistrationCommand cmd) {
        UUID sagaId = cmd.sagaId();
        try {
            UserRegistrationState state = userRegistrationStateService.getOrStartSaga(sagaId, cmd.email(), cmd.roles());
            if (state.getStep() != UserRegistrationStep.STARTED) {
                return;
            }
            RegisterResponse response = registerService.register(sagaId,
                    cmd.email(),
                    cmd.password(),
                    cmd.roles());
            userRegistrationStateService.markCreated(sagaId,
                    response.getAuthResponse().getUser().getId(),
                    response.getAuthResponse().getToken(),
                    response.getAuthResponse().getRefreshToken(),
                    response.isNewUser());
            if (response.isNewUser()) {
                publishAwaitingVerificationUserRegistrationReply(sagaId);
                userRegistrationStateService.markPendingVerification(sagaId);
            } else {
                publishSuccessUserRegistrationReply(sagaId, response.getAuthResponse().getToken(), response.getAuthResponse().getRefreshToken());
            }
        }
        catch (Exception ex) {
            SagaErrorMapper.SagaError error = SagaErrorMapper.map(ex);
            publishFailureUserRegistrationReply(sagaId, error.code(), error.message());
            recoverUserRegistrationService.recoverUserRegistration(sagaId);
        }
    }

    private void publishSuccessUserRegistrationReply(UUID sagaId,
                                                     String token,
                                                     String refreshToken) {
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
        publisher.publishFailureUserRegistrationReply(
                FailureUserRegistrationReply.builder()
                        .sagaId(sagaId)
                        .status(status)
                        .message(message)
                        .build());
    }

    private void publishAwaitingVerificationUserRegistrationReply(UUID sagaId) {
        publisher.publishAwaitingVerificationUserRegistrationReply(
                AwaitingVerificationUserRegistrationReply.builder()
                        .sagaId(sagaId)
                        .build());
    }

}
