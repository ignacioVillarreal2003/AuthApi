package com.api.authapi.application.helpers;

import com.api.authapi.application.exceptions.notFound.UserNotFoundException;
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
        return userRepository.findByEmail(username)
                .orElseThrow(UserNotFoundException::new);
    }
}
