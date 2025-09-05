package dev.ignacio.villarreal.authenticationapi.application.services.user;

import dev.ignacio.villarreal.authenticationapi.application.helpers.UserHelperService;
import dev.ignacio.villarreal.authenticationapi.domain.model.User;
import dev.ignacio.villarreal.authenticationapi.infrastructure.persistence.repositories.UserRepository;
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
        log.debug("Attempting to deactivate account for user {}", user.getEmail());

        if (user.isEnabled()) {
            user.setEnabled(false);
            userRepository.save(user);
            log.info("Account deactivated for user {}", user.getEmail());
        } else {
            log.info("Account already deactivated for user {}", user.getEmail());
        }
    }
}
