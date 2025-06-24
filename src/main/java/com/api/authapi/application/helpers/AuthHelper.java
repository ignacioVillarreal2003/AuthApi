package com.api.authapi.application.helpers;

import com.api.authapi.application.mappers.UserResponseMapper;
import com.api.authapi.config.JwtService;
import com.api.authapi.domain.dtos.user.AuthResponse;
import com.api.authapi.domain.models.User;
import com.api.authapi.infraestructure.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthHelper {

    private final UserRepository userRepository;
    private final UserResponseMapper userResponseMapper;
    private final JwtService jwtService;

    public AuthResponse getAuthResponse(User user) {
        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return AuthResponse.builder()
                .token(token)
                .refreshToken(user.getRefreshToken())
                .user(userResponseMapper.apply(user))
                .build();
    }
}
