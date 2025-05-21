package com.api.authapi.domain.dtos;

import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @Size(min = 8, max = 64, message = "Last password must be between 8 and 64 characters")
        String lastPassword,

        @Size(min = 8, max = 64, message = "New password must be between 8 and 64 characters")
        String newPassword
) {
}
