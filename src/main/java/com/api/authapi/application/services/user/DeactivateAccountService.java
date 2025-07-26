package com.api.authapi.application.services.user;

import com.api.authapi.application.helpers.UserHelperService;
import com.api.authapi.domain.model.User;
import com.api.authapi.infrastructure.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeactivateAccountService {

    private final UserHelperService userHelperService;
    private final UserRepository userRepository;

    public void execute() {
        User user = userHelperService.getCurrentUser();
        userHelperService.verifyAccountStatus(user);

        if (user.isEnabled()) {
            user.setEnabled(false);
            userRepository.save(user);
        }
    }
}
