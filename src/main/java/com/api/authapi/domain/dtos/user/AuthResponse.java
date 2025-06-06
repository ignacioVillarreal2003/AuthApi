package com.api.authapi.domain.dtos.user;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AuthResponse {
    private String token;
    private String refreshToken;
    private UserResponse user;
}