package dev.ignacio.villarreal.authenticationapi.application.services.refreshToken;

import dev.ignacio.villarreal.authenticationapi.config.properties.JwtProperties;
import dev.ignacio.villarreal.authenticationapi.domain.model.RefreshToken;
import dev.ignacio.villarreal.authenticationapi.domain.model.User;
import dev.ignacio.villarreal.authenticationapi.infrastructure.persistence.repositories.RefreshTokenRepository;
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
