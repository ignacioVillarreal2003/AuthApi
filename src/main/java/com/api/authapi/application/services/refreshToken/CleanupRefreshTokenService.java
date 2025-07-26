package com.api.authapi.application.services.refreshToken;

import com.api.authapi.domain.model.RefreshToken;
import com.api.authapi.infrastructure.persistence.repositories.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CleanupRefreshTokenService {

    private final RefreshTokenRepository repository;

    public void execute(Long userId) {
        List<RefreshToken> refreshTokens = repository.findAllByUser_Id(userId);
        for (RefreshToken refreshToken : refreshTokens) {
            if (!refreshToken.isRevoked()) {
                refreshToken.setRevoked(true);
            }
        }
        repository.saveAll(refreshTokens);
    }
}
