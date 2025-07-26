package com.api.authapi.application.services.user;

import com.api.authapi.application.exceptions.TokenExpiredException;
import com.api.authapi.application.exceptions.UserNotFoundException;
import com.api.authapi.domain.model.User;
import com.api.authapi.infrastructure.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActivateAccountService {

    private final UserRepository userRepository;

    public void execute(UUID activationToken) {
        User user = userRepository.findByActivationToken(activationToken)
                .orElseThrow(UserNotFoundException::new);

        if (user.getActivationTokenExpiration().isBefore(Instant.now())) {
            throw new TokenExpiredException();
        }

        if (!user.isEnabled()) {
            user.setEnabled(true);
            user.setActivationToken(null);
            user.setActivationTokenExpiration(null);
            userRepository.save(user);
        }
    }
}
