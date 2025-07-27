package com.api.authapi.application.services.authentication;

import com.api.authapi.application.mappers.UserResponseMapper;
import com.api.authapi.application.services.refreshToken.RefreshTokenCreationService;
import com.api.authapi.config.authentication.JwtService;
import com.api.authapi.domain.dto.auth.AuthResponse;
import com.api.authapi.domain.model.RefreshToken;
import com.api.authapi.domain.model.User;
import com.api.authapi.infrastructure.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthResponseBuilderService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserResponseMapper userResponseMapper;
    private final RefreshTokenCreationService refreshTokenCreationService;

    public AuthResponse generateAuthResponse(User user) {
        log.info("[AuthResponseBuilderService::generateAuthResponse] - Building authentication response");

        String token = jwtService.generateToken(user);
        RefreshToken refreshToken = refreshTokenCreationService.create(
                jwtService.generateRefreshToken(user),
                user);

        userRepository.save(user);

        log.debug("[AuthResponseBuilderService::generateAuthResponse] - Tokens generated and saved");

        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken.getToken())
                .user(userResponseMapper.apply(user))
                .build();
    }
}
