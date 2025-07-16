package com.api.authapi.application.helpers;

import com.api.authapi.application.exceptions.UserNotFoundException;
import com.api.authapi.infrastructure.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        log.info("[CustomUserDetailsService::loadUserByUsername] Attempting to load user: {}", username);
        return userRepository.findByEmail(username)
                .orElseThrow(() -> {
                    log.warn("[CustomUserDetailsService::loadUserByUsername] User not found: {}", username);
                    return new UserNotFoundException();
                });
    }
}
