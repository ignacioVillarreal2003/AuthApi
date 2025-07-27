package com.api.authapi.application.services.refreshToken;

import com.api.authapi.application.exceptions.RefreshTokenNotFoundException;
import com.api.authapi.domain.model.RefreshToken;
import com.api.authapi.infrastructure.persistence.repositories.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenRevocationService {

    private final RefreshTokenRepository repository;

    public void revokeById(Long id) {
        log.debug("[RefreshTokenRevocationService::revokeById] - Revoking token by ID {}", id);

        RefreshToken token = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[RefreshTokenRevocationService::revokeById] - Token not found");
                    return new RefreshTokenNotFoundException();
                });

        if (!token.isRevoked()) {
            token.setRevoked(true);
            repository.save(token);
            log.info("[RefreshTokenRevocationService::revokeById] - Token revoked");
        } else {
            log.info("[RefreshTokenRevocationService::revokeById] - Token already revoked");
        }
    }

    public void revokeAllByUserId(Long userId) {
        log.debug("[RefreshTokenRevocationService::revokeAllByUserId] - Revoking all tokens for user ID {}", userId);

        List<RefreshToken> tokens = repository.findAllByUser_Id(userId);
        boolean anyRevoked = false;

        for (RefreshToken token : tokens) {
            if (!token.isRevoked()) {
                token.setRevoked(true);
                anyRevoked = true;
            }
        }

        if (anyRevoked) {
            repository.saveAll(tokens);
            log.info("[RefreshTokenRevocationService::revokeAllByUserId] - Revoked {} tokens", tokens.size());
        } else {
            log.info("[RefreshTokenRevocationService::revokeAllByUserId] - No tokens to revoke");
        }
    }
}
