package com.api.authapi.application.services.refreshToken;

import com.api.authapi.application.exceptions.notFound.RefreshTokenNotFoundException;
import com.api.authapi.domain.model.RefreshToken;
import com.api.authapi.infrastructure.persistence.repositories.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenRetrievalService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken getByToken(String token) {
        log.debug("Looking up refresh token '{}'", token);

        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> {
                    log.warn("Refresh token '{}' not found", token);
                    return new RefreshTokenNotFoundException();
                });
    }
}
