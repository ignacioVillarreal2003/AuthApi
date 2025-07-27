package com.api.authapi.application.services.user;

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
        log.debug("[UserDeletionService::deleteById] - Deleting user");
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            log.info("[UserDeletionService::deleteById] - User deleted");
        } else {
            log.warn("[UserDeletionService::deleteById] - User not found");
        }
    }
}
