package com.api.authapi.application.services.refreshToken;

import com.api.authapi.application.exceptions.RefreshTokenNotFoundException;
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
        log.debug("[RefreshTokenRetrievalService::getByToken] - Looking up refresh token");

        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> {
                    log.warn("[RefreshTokenRetrievalService::getByToken] - Token not found");
                    return new RefreshTokenNotFoundException();
                });
    }
}
