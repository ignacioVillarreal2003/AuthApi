package dev.ignacio.villarreal.authenticationapi.domain.dto.user;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record VerifyAccountRequest(
        @NotNull(message = "Activation token is required")
        UUID activationToken,

        @NotNull(message = "Saga id is required")
        UUID sagaId
) {
}
