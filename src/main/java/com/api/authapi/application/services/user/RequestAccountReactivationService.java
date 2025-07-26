package com.api.authapi.application.services.user;

import com.api.authapi.application.exceptions.UserAlreadyEnabledException;
import com.api.authapi.application.exceptions.UserNotFoundException;
import com.api.authapi.application.helpers.EmailService;
import com.api.authapi.domain.dto.user.ReactivationRequest;
import com.api.authapi.domain.model.User;
import com.api.authapi.infrastructure.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RequestAccountReactivationService {

    private final UserRepository userRepository;
    private final EmailService emailService;

    public void execute(ReactivationRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(UserNotFoundException::new);

        if (user.isEnabled()) throw new UserAlreadyEnabledException();

        UUID token = UUID.randomUUID();

        user.setActivationToken(token);
        user.setActivationTokenExpiration(Instant.now().plus(Duration.ofHours(24)));
        userRepository.save(user);

        emailService.sendReactivationEmail(user.getEmail(), token);
    }

}
