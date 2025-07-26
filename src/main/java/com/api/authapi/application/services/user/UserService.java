package com.api.authapi.application.services.user;

import com.api.authapi.domain.dto.user.ReactivationRequest;
import com.api.authapi.domain.dto.user.UpdatePasswordRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final DeactivateAccountService deactivateAccountService;
    private final ActivateAccountService activateAccountService;
    private final RequestAccountReactivationService requestAccountReactivationService;
    private final ChangePasswordService changePasswordService;

    @Transactional
    public void deactivateAccount() {
        deactivateAccountService.execute();
    }

    @Transactional
    public void activateAccount(UUID activationToken) {
        activateAccountService.execute(activationToken);
    }

    @Transactional
    public void requestAccountReactivation(ReactivationRequest request) {
        requestAccountReactivationService.execute(request);
    }

    @Transactional
    public void changePassword(UpdatePasswordRequest request) {
        changePasswordService.execute(request);
    }
}
