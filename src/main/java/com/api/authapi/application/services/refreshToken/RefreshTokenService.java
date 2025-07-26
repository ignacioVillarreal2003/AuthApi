package com.api.authapi.application.services.refreshToken;

import com.api.authapi.domain.model.RefreshToken;
import com.api.authapi.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final CreateRefreshTokenService createRefreshTokenService;
    private final GetRefreshTokenByTokenService getRefreshTokenByTokenService;
    private final RevokeRefreshTokenService revokeRefreshTokenService;
    private final CleanupRefreshTokenService cleanupRefreshTokenService;

    @Transactional
    public RefreshToken getRefreshTokenByToken(String token) {
        return getRefreshTokenByTokenService.execute(token);
    }

    @Transactional
    public RefreshToken createRefreshToken(String token, User user) {
        return createRefreshTokenService.execute(token, user);
    }

    @Transactional
    public void revokeRefreshToken(Long id) {
        revokeRefreshTokenService.execute(id);
    }

    @Transactional
    public void cleanupRefreshToken(Long userId) {
        cleanupRefreshTokenService.execute(userId);
    }
}
