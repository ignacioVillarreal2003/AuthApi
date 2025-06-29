package com.api.authapi.config.authentication;

import com.api.authapi.domain.models.User;
import com.api.authapi.infraestructure.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class AuthenticationUserProvider {

    private final UserRepository userRepository;

    public Long getUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserPrincipal userPrincipal) {
            return userPrincipal.getUserId();
        }
        return null;
    }

    public User getAuthenticatedUser() {
        Long userId = getUserId();
        return userRepository.findByIdAndEnabledTrue(userId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated"));
    }
}
