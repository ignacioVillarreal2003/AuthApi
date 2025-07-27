package com.api.authapi.application.services.user;

import com.api.authapi.application.helpers.UserHelperService;
import com.api.authapi.domain.model.User;
import com.api.authapi.infrastructure.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountDeactivationService {

    private final UserHelperService userHelperService;
    private final UserRepository userRepository;

    public void deactivateAccount() {
        User user = userHelperService.getCurrentUser();
        userHelperService.verifyAccountStatus(user);

        if (user.isEnabled()) {
            user.setEnabled(false);
            userRepository.save(user);
            log.info("[AccountDeactivationService::deactivateAccount] - Account deactivated");
        } else {
            log.info("[AccountDeactivationService::deactivateAccount] - Account already deactivated");
        }
    }
}
