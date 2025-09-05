package dev.ignacio.villarreal.authenticationapi.application.services.authentication;

import dev.ignacio.villarreal.authenticationapi.application.mappers.UserResponseMapper;
import dev.ignacio.villarreal.authenticationapi.application.services.refreshToken.RefreshTokenCreationService;
import dev.ignacio.villarreal.authenticationapi.config.authentication.JwtService;
import dev.ignacio.villarreal.authenticationapi.domain.dto.auth.AuthResponse;
import dev.ignacio.villarreal.authenticationapi.domain.model.RefreshToken;
import dev.ignacio.villarreal.authenticationapi.domain.model.User;
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
