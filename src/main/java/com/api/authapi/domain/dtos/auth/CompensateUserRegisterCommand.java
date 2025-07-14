package com.api.authapi.domain.dtos.auth;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CompensateUserRegisterCommand(
        @NotNull(message = "Saga id is required")
        UUID sagaId,

        @NotNull(message = "Reason is required")
        String reason
) {
}
