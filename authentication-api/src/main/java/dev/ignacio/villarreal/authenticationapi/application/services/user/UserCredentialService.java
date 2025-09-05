package dev.ignacio.villarreal.authenticationapi.application.services.user;

import dev.ignacio.villarreal.authenticationapi.application.exceptions.conflict.PasswordAlreadyUsedException;
import dev.ignacio.villarreal.authenticationapi.application.exceptions.unauthorized.InvalidCredentialsException;
import dev.ignacio.villarreal.authenticationapi.application.helpers.UserHelperService;
import dev.ignacio.villarreal.authenticationapi.domain.model.User;
import dev.ignacio.villarreal.authenticationapi.infrastructure.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCredentialService {

    private final UserHelperService userHelperService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void updatePassword(String lastPassword, String newPassword) {
        User user = userHelperService.getCurrentUser();
        log.debug("Attempting password change for user {}", user.getEmail());

        if (!passwordEncoder.matches(lastPassword, user.getPassword())) {
            log.warn("Invalid password provided for user {}", user.getEmail());
            throw new InvalidCredentialsException();
        }

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            log.warn("New password is the same as current password for user {}", user.getEmail());
            throw new PasswordAlreadyUsedException();
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setLastPasswordChange(Instant.now());
        userRepository.save(user);

        log.info("Password updated for user {}", user.getEmail());
    }
}
