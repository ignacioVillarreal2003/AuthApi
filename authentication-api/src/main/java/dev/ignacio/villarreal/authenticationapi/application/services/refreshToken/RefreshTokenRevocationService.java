package dev.ignacio.villarreal.authenticationapi.application.services.refreshToken;

import dev.ignacio.villarreal.authenticationapi.application.exceptions.notFound.RefreshTokenNotFoundException;
import dev.ignacio.villarreal.authenticationapi.domain.model.RefreshToken;
import dev.ignacio.villarreal.authenticationapi.infrastructure.persistence.repositories.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenRevocationService {

    private final RefreshTokenRepository refreshTokenRepository;

    public void revokeById(Long id) {
        log.debug("Revoking refresh token with ID {}", id);

        RefreshToken token = refreshTokenRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Refresh token with ID {} not found", id);
                    return new RefreshTokenNotFoundException();
                });

        if (!token.isRevoked()) {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
            log.info("Refresh token with ID {} successfully revoked", id);
        }
        else {
            log.info("Refresh token with ID {} was already revoked", id);
        }
    }

    public void revokeAllByUserId(Long userId) {
        log.debug("Revoking all refresh tokens for user ID {}", userId);

        List<RefreshToken> tokens = refreshTokenRepository.findAllByUser_Id(userId);
        List<RefreshToken> activeTokens = tokens.stream()
                .filter(t -> !t.isRevoked())
                .toList();

        for (RefreshToken token : activeTokens) {
            token.setRevoked(true);
        }

        if (!activeTokens.isEmpty()) {
            refreshTokenRepository.saveAll(activeTokens);
            log.info("Revoked {} refresh tokens for user ID {}", activeTokens.size(), userId);
        }
        else {
            log.info("No active refresh tokens to revoke for user ID {}", userId);
        }
    }
}
