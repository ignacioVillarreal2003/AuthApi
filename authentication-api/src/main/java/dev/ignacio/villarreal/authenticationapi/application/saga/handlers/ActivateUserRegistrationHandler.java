package dev.ignacio.villarreal.authenticationapi.application.saga.handlers;

import dev.ignacio.villarreal.authenticationapi.application.saga.helpers.SagaErrorMapper;
import dev.ignacio.villarreal.authenticationapi.application.saga.services.UserRegistrationStateService;
import dev.ignacio.villarreal.authenticationapi.domain.saga.reply.FailureUserRegistrationReply;
import dev.ignacio.villarreal.authenticationapi.domain.saga.reply.SuccessUserRegistrationReply;
import dev.ignacio.villarreal.authenticationapi.domain.saga.state.UserRegistrationState;
import dev.ignacio.villarreal.authenticationapi.domain.saga.step.UserRegistrationStep;
import dev.ignacio.villarreal.authenticationapi.infrastructure.messaging.publisher.UserRegistrationPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActivateUserRegistrationHandler {

    private final UserRegistrationStateService userRegistrationStateService;
    private final UserRegistrationPublisher publisher;
    private final RecoverUserRegistrationHandler recoverUserRegistrationService;

    public void activateUserRegistration(UUID sagaId) {
        try {
            UserRegistrationState state = userRegistrationStateService.getSagaState(sagaId);
            if (state.getStep() != UserRegistrationStep.PENDING_VERIFICATION) {
                return;
            }
            publishSuccessUserRegistrationReply(sagaId, state.getToken(), state.getRefreshToken());
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
}
