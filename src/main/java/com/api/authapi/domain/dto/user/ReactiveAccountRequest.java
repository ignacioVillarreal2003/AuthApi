package com.api.authapi.domain.dto.user;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ReactiveAccountRequest (
        @NotNull(message = "Activation token is required")
        UUID activationToken
) {
}
