package com.api.authapi.application.services.refreshToken;

import com.api.authapi.config.properties.JwtProperties;
import com.api.authapi.domain.model.RefreshToken;
import com.api.authapi.domain.model.User;
import com.api.authapi.infrastructure.persistence.repositories.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CreateRefreshTokenService {

    private final RefreshTokenRepository repository;
    private final JwtProperties jwtProperties;

    public RefreshToken execute(String token, User user) {
        return repository.save(
                RefreshToken.builder()
                        .token(token)
                        .expiresAt(Instant.now()
                                .plusMillis(jwtProperties.getExpiration()
                                        .getRefreshTokenMs()))
                        .user(user)
                        .build()
        );
    }
}
