package dev.ignacio.villarreal.authenticationapi.domain.dto.auth;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RegisterResponse {
    private AuthResponse authResponse;
    private boolean isNew;
}
