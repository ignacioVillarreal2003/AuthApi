package com.api.authapi.domain.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LoginUserRequest(
        @NotNull(message = "Email is required")
        @Email(message = "Email is invalid")
        @Size(max = 64, message = "Email must be less than 64 characters")
        String email,

        @NotNull(message = "Password is required")
        @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
        String password
) {
}
