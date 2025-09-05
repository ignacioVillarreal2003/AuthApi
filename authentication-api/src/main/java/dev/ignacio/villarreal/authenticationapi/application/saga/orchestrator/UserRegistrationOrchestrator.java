package dev.ignacio.villarreal.authenticationapi.application.saga.orchestrator;

import dev.ignacio.villarreal.authenticationapi.application.saga.handlers.ConfirmUserRegistrationHandler;
import dev.ignacio.villarreal.authenticationapi.application.saga.handlers.InitiateUserRegistrationHandler;
import dev.ignacio.villarreal.authenticationapi.application.saga.handlers.RecoverUserRegistrationHandler;
import dev.ignacio.villarreal.authenticationapi.application.saga.handlers.RollbackUserRegistrationHandler;
import dev.ignacio.villarreal.authenticationapi.domain.saga.command.ConfirmUserRegistrationCommand;
import dev.ignacio.villarreal.authenticationapi.domain.saga.command.InitiateUserRegistrationCommand;
import dev.ignacio.villarreal.authenticationapi.domain.saga.command.RollbackUserRegistrationCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserRegistrationOrchestrator {

    private final InitiateUserRegistrationHandler initiateUserRegistrationHandler;
    private final RollbackUserRegistrationHandler rollbackUserRegistrationHandler;
    private final RecoverUserRegistrationHandler recoverUserRegistrationHandler;
    private final ConfirmUserRegistrationHandler confirmUserRegistrationHandler;

    public void handleInitiateUserRegistrationCommand(InitiateUserRegistrationCommand cmd) {
        initiateUserRegistrationHandler.handle(cmd);
    }

    public void handleRollbackUserRegistrationCommand(RollbackUserRegistrationCommand cmd) {
        rollbackUserRegistrationHandler.rollbackUserRegistration(cmd);
    }

    public void handleConfirmUserRegistrationCommand(ConfirmUserRegistrationCommand cmd) {
        confirmUserRegistrationHandler.confirmUserRegistration(cmd);
    }

    public void recoverCommand(UUID sagaId) {
        recoverUserRegistrationHandler.recoverUserRegistration(sagaId);
    }
}
