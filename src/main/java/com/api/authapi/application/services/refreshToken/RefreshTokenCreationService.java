package com.api.authapi.application.services.refreshToken;

import com.api.authapi.config.properties.JwtProperties;
import com.api.authapi.domain.model.RefreshToken;
import com.api.authapi.domain.model.User;
import com.api.authapi.infrastructure.persistence.repositories.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenCreationService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    public RefreshToken create(String token, User user) {
        log.debug("Creating refresh token for user {}", user.getEmail());

        RefreshToken refreshToken = refreshTokenRepository.save(
                RefreshToken.builder()
                        .token(token)
                        .expiresAt(Instant.now()
                                .plusMillis(jwtProperties
                                        .getExpiration()
                                        .getRefreshTokenMs()))
                        .user(user)
                        .build());

        log.info("Refresh token created for user {} with expiration at {}", user.getEmail(), refreshToken.getExpiresAt());

        return refreshToken;
    }
}
