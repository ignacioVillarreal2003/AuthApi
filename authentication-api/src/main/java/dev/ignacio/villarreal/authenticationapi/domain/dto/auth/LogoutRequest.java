package dev.ignacio.villarreal.authenticationapi.domain.dto.auth;

import jakarta.validation.constraints.NotNull;

public record LogoutRequest(
        @NotNull(message = "Refresh token is required")
        String refreshToken
) {
}
