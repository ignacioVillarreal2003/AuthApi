package com.api.authapi.domain.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RequestAccountReactivationRequest(
        @NotNull(message = "Email is required")
        @Email(message = "Email is invalid")
        @Size(max = 100, message = "Email must be less than 100 characters")
        String email
) {
}
