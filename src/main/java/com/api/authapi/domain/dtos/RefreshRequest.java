package com.api.authapi.domain.dtos;

import jakarta.validation.constraints.NotNull;

public record RefreshRequest (
        @NotNull(message = "Refresh token is required")
        String refreshToken
) {
}
