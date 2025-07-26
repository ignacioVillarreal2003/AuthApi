package com.api.authapi.application.services.auth;

import com.api.authapi.application.mappers.UserResponseMapper;
import com.api.authapi.application.services.refreshToken.CreateRefreshTokenService;
import com.api.authapi.config.authentication.JwtService;
import com.api.authapi.domain.dto.auth.AuthResponse;
import com.api.authapi.domain.model.RefreshToken;
import com.api.authapi.domain.model.User;
import com.api.authapi.infrastructure.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BuildAuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserResponseMapper userResponseMapper;
    private final CreateRefreshTokenService createRefreshTokenService;

    public AuthResponse execute(User user) {
        String token = jwtService.generateToken(user);
        RefreshToken refreshToken = createRefreshTokenService.execute(
                jwtService.generateRefreshToken(user),
                user);
        userRepository.save(user);
        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken.getToken())
                .user(userResponseMapper.apply(user))
                .build();
    }
}
