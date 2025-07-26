package com.api.authapi.domain.saga;

import com.api.authapi.domain.dto.auth.AuthResponse;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RegisterResult {
    private AuthResponse authResponse;
    private boolean isNewUser;
}
