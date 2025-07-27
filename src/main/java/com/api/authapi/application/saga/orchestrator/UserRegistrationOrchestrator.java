package com.api.authapi.application.saga.orchestrator;

import com.api.authapi.application.saga.handlers.*;
import com.api.authapi.domain.saga.command.ConfirmUserRegistrationCommand;
import com.api.authapi.domain.saga.command.RollbackUserRegistrationCommand;
import com.api.authapi.domain.saga.command.InitiateUserRegistrationCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserRegistrationOrchestrator {

    private final InitiateUserRegistrationHandler initiateUserRegistrationHandler;
    private final ActivateUserRegistrationHandler activateUserRegistrationHandler;
    private final RollbackUserRegistrationHandler rollbackUserRegistrationHandler;
    private final RecoverUserRegistrationHandler recoverUserRegistrationHandler;
    private final ConfirmUserRegistrationHandler confirmUserRegistrationHandler;

    public void handleInitiateUserRegistrationCommand(InitiateUserRegistrationCommand cmd) {
        initiateUserRegistrationHandler.handle(cmd);
    }

    public void handleActiveAccount(UUID sagaId) {
        activateUserRegistrationHandler.activateUserRegistration(sagaId);
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
