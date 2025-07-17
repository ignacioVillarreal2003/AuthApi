package com.api.authapi.domain.saga.command;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RollbackUserRegistrationCommand(
        @NotNull(message = "Saga id is required")
        UUID sagaId
) {
}
