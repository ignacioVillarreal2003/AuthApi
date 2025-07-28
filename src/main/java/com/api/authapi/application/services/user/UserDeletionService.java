package com.api.authapi.application.services.user;

import com.api.authapi.application.exceptions.notFound.UserNotFoundException;
import com.api.authapi.infrastructure.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDeletionService {

    private final UserRepository userRepository;

    public void deleteById(Long id) {
        log.debug("Attempting to delete user with ID {}", id);

        if (!userRepository.existsById(id)) {
            log.warn("User with ID {} not found", id);
            throw new UserNotFoundException();
        }

        userRepository.deleteById(id);
        log.info("User with ID {} deleted", id);
    }
}
