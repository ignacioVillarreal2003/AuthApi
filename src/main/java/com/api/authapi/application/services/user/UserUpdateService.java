package com.api.authapi.application.services.user;

import com.api.authapi.application.exceptions.InvalidCredentialsException;
import com.api.authapi.application.helpers.UserHelperService;
import com.api.authapi.domain.model.User;
import com.api.authapi.infrastructure.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserUpdateService {

    private final UserHelperService userHelperService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void changePassword(String lastPassword, String newPassword) {
        log.debug("[UserUpdateService::changePassword] - Attempting password change");

        User user = userHelperService.getCurrentUser();
        userHelperService.verifyAccountStatus(user);

        if (!passwordEncoder.matches(lastPassword, user.getPassword())) {
            log.warn("[UserUpdateService::changePassword] - Invalid credentials");
            throw new InvalidCredentialsException();
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setLastPasswordChange(Instant.now());
        userRepository.save(user);

        log.info("[UserUpdateService::changePassword] - Password updated");
    }
}
