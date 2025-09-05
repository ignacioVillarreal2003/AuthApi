package dev.ignacio.villarreal.authenticationapi.domain.dto.auth;

import jakarta.validation.constraints.NotNull;

public record RefreshSessionRequest(
        @NotNull(message = "Refresh token is required")
        String refreshToken
) {
}
