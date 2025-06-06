package com.api.authapi.domain.dtos.user;

import jakarta.validation.constraints.NotNull;

public record RefreshRequest (
        @NotNull(message = "Refresh token is required")
        String refreshToken
) {
}
