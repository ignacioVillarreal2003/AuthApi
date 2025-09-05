package dev.ignacio.villarreal.authenticationapi.domain.dto.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotNull(message = "Last password is required")
        @Size(min = 8, max = 64, message = "Last password must be between 8 and 64 characters")
        String lastPassword,

        @NotNull(message = "New password is required")
        @Size(min = 8, max = 64, message = "New password must be between 8 and 64 characters")
        String newPassword
) {
}
