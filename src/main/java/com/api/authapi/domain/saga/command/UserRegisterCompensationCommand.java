package com.api.authapi.domain.saga.command;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UserRegisterCompensationCommand(
        @NotNull(message = "Saga id is required")
        UUID sagaId,

        @NotNull(message = "Reason is required")
        String reason
) {
}
