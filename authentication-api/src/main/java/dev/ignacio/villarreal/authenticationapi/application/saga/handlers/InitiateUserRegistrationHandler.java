package dev.ignacio.villarreal.authenticationapi.application.saga.handlers;

import dev.ignacio.villarreal.authenticationapi.application.saga.helpers.SagaErrorMapper;
import dev.ignacio.villarreal.authenticationapi.application.saga.services.UserRegistrationStateService;
import dev.ignacio.villarreal.authenticationapi.application.services.authentication.RegisterService;
import dev.ignacio.villarreal.authenticationapi.domain.dto.auth.RegisterResponse;
import dev.ignacio.villarreal.authenticationapi.domain.saga.command.InitiateUserRegistrationCommand;
import dev.ignacio.villarreal.authenticationapi.domain.saga.reply.AwaitingVerificationUserRegistrationReply;
import dev.ignacio.villarreal.authenticationapi.domain.saga.reply.FailureUserRegistrationReply;
import dev.ignacio.villarreal.authenticationapi.domain.saga.reply.SuccessUserRegistrationReply;
import dev.ignacio.villarreal.authenticationapi.domain.saga.state.UserRegistrationState;
import dev.ignacio.villarreal.authenticationapi.domain.saga.step.UserRegistrationStep;
import dev.ignacio.villarreal.authenticationapi.infrastructure.messaging.publisher.UserRegistrationPublisher;
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
                    response.isNew());
            if (response.isNew()) {
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
