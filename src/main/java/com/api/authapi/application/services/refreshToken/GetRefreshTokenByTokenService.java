package com.api.authapi.application.services.refreshToken;

import com.api.authapi.application.exceptions.RefreshTokenNotFoundException;
import com.api.authapi.domain.model.RefreshToken;
import com.api.authapi.infrastructure.persistence.repositories.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetRefreshTokenByTokenService {

    private final RefreshTokenRepository repository;

    public RefreshToken execute(String token) {
        return repository.findByToken(token)
                .orElseThrow(RefreshTokenNotFoundException::new);
    }
}
