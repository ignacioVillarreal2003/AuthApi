package com.api.authapi.application.services.refreshToken;

import com.api.authapi.application.exceptions.RefreshTokenNotFoundException;
import com.api.authapi.domain.model.RefreshToken;
import com.api.authapi.infrastructure.persistence.repositories.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RevokeRefreshTokenService {

    private final RefreshTokenRepository repository;

    public void execute(Long id) {
        RefreshToken refreshToken = repository.findById(id)
                .orElseThrow(RefreshTokenNotFoundException::new);
        if (!refreshToken.isRevoked()) {
            refreshToken.setRevoked(true);
            repository.save(refreshToken);
        }
    }
}
