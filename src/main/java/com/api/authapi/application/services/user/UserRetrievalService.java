package com.api.authapi.application.services.user;

import com.api.authapi.application.exceptions.UserNotFoundException;
import com.api.authapi.domain.model.User;
import com.api.authapi.infrastructure.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserRetrievalService {

    private final UserRepository userRepository;

    public User getById(Long id) {
        log.debug("[UserRetrievalService::getById] - Retrieving user");
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[UserRetrievalService::getById] - User not found");
                    return new UserNotFoundException();
                });
    }
}
