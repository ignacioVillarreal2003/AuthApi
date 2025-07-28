package com.api.authapi.application.services.authentication;

import com.api.authapi.application.mappers.UserResponseMapper;
import com.api.authapi.application.services.refreshToken.RefreshTokenCreationService;
import com.api.authapi.config.authentication.JwtService;
import com.api.authapi.domain.dto.auth.AuthResponse;
import com.api.authapi.domain.model.RefreshToken;
import com.api.authapi.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthResponseBuilderService {

    private final JwtService jwtService;
    private final UserResponseMapper userResponseMapper;
    private final RefreshTokenCreationService refreshTokenCreationService;

    public AuthResponse build(User user) {
        log.info("Generating auth response for user: {}", user.getEmail());

        String jwt = jwtService.generateToken(user);
        String refresh = jwtService.generateRefreshToken(user);

        RefreshToken refreshToken = refreshTokenCreationService.create(refresh, user);

        log.debug("Tokens generated and saved for user: {}", user.getEmail());

        return AuthResponse.builder()
                .token(jwt)
                .refreshToken(refreshToken.getToken())
                .user(userResponseMapper.apply(user))
                .build();
    }
}
