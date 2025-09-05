package dev.ignacio.villarreal.authenticationapi.application.services.user;

import dev.ignacio.villarreal.authenticationapi.application.exceptions.notFound.UserNotFoundException;
import dev.ignacio.villarreal.authenticationapi.domain.model.User;
import dev.ignacio.villarreal.authenticationapi.infrastructure.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserRetrievalService {

    private final UserRepository userRepository;

    public User getById(Long id) {
        log.debug("Retrieving user with ID {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User with ID {} not found", id);
                    return new UserNotFoundException();
                });
    }
}
