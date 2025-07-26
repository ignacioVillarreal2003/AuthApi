package com.api.authapi.application.services.auth;

import com.api.authapi.domain.model.User;
import com.api.authapi.infrastructure.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegisterRollbackService {

    private final UserRepository userRepository;

    public void execute(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return;
        }
        User user = userOpt.get();
        userRepository.delete(user);
    }
}
