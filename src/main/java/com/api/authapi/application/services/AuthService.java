package com.api.authapi.application.services;

import com.api.authapi.application.helpers.AuthHelper;
import com.api.authapi.config.AuthenticatedUserProvider;
import com.api.authapi.domain.dtos.user.*;
import com.api.authapi.domain.models.User;
import com.api.authapi.infraestructure.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final AuthHelper authHelper;
    private final AuthenticationManager authenticationManager;
    private final AuthenticatedUserProvider authenticatedUserProvider;


    public AuthResponse login(LoginUserRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );
        }
        catch (BadCredentialsException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        User user = userRepository.findByEmailAndEnabledTrue(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return authHelper.getAuthResponse(user);
    }

    public void logout() {
        User existingUser = authenticatedUserProvider.getAuthenticatedUser();

        existingUser.setRefreshToken(null);

        userRepository.save(existingUser);
    }

    public AuthResponse refresh(RefreshTokenRequest request) {
        User existingUser = authenticatedUserProvider.getAuthenticatedUser();

        if (!existingUser.getRefreshToken().equals(request.refreshToken())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token is incorrect");
        }

        return authHelper.getAuthResponse(existingUser);
    }
}
