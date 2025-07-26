package com.api.authapi.application.services.user;

import com.api.authapi.application.exceptions.InvalidCredentialsException;
import com.api.authapi.application.helpers.UserHelperService;
import com.api.authapi.domain.dto.user.UpdatePasswordRequest;
import com.api.authapi.domain.model.User;
import com.api.authapi.infrastructure.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ChangePasswordService {

    private final UserHelperService userHelperService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void execute(UpdatePasswordRequest request) {
        User user = userHelperService.getCurrentUser();
        userHelperService.verifyAccountStatus(user);

        if (!passwordEncoder.matches(request.lastPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user.setLastPasswordChange(Instant.now());
        userRepository.save(user);
    }
}
