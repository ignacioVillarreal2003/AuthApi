package dev.ignacio.villarreal.authenticationapi.application.helpers;

import dev.ignacio.villarreal.authenticationapi.application.exceptions.notFound.UserNotFoundException;
import dev.ignacio.villarreal.authenticationapi.infrastructure.persistence.repositories.UserRepository;
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
