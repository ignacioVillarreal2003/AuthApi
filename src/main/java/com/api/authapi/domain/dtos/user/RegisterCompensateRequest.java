package com.api.authapi.domain.dtos.user;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RegisterCompensateRequest(
    @NotNull(message = "Saga id is required")
    UUID sagaId,

    @NotNull(message = "Reason is required")
    String reason,

    @NotNull(message = "User id is required")
    Long userId
) {
}
