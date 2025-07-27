package com.api.authapi.application.services.user;

import com.api.authapi.domain.dto.user.ReactivationRequest;
import com.api.authapi.domain.dto.user.ChangePasswordRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AccountDeactivationService accountDeactivationService;
    private final AccountActivationService accountActivationService;
    private final UserUpdateService userUpdateService;

    @Transactional
    public void deactivateAccount() {
        accountDeactivationService.deactivateAccount();
    }

    @Transactional
    public void activateAccount(UUID token) {
        accountActivationService.activateAccount(token);
    }

    @Transactional
    public void requestAccountReactivation(ReactivationRequest request) {
        accountActivationService.requestAccountReactivation(request.email());
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        userUpdateService.changePassword(request.lastPassword(), request.newPassword());
    }
}
