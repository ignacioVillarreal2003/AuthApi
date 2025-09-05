package dev.ignacio.villarreal.authenticationapi.domain.dto.auth;

import dev.ignacio.villarreal.authenticationapi.domain.dto.user.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse implements Serializable {
    private String token;
    private String refreshToken;
    private UserResponse user;
}