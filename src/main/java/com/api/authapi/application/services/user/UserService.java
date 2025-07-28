package com.api.authapi.application.services.user;

import com.api.authapi.config.annotations.RequireActiveAccount;
import com.api.authapi.domain.dto.user.VerifyAccountRequest;
import com.api.authapi.domain.dto.user.ReactiveAccountRequest;
import com.api.authapi.domain.dto.user.RequestAccountReactivationRequest;
import com.api.authapi.domain.dto.user.ChangePasswordRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AccountDeactivationService accountDeactivationService;
    private final AccountActivationService accountActivationService;
    private final UserCredentialService userCredentialService;

    @RequireActiveAccount
    @Transactional
    public void deactivateAccount() {
        accountDeactivationService.deactivateAccount();
    }

    @Transactional
    public void reactivateAccount(ReactiveAccountRequest request) {
        accountActivationService.reactivateAccountByToken(request.activationToken());
    }

    @Transactional
    public void verifyAccount(@Valid VerifyAccountRequest request) {
        accountActivationService.verifyAccountByToken(request.activationToken(),
                request.sagaId());
    }

    @Transactional
    public void requestAccountReactivation(RequestAccountReactivationRequest request) {
        accountActivationService.requestAccountReactivation(request.email());
    }

    @RequireActiveAccount
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        userCredentialService.updatePassword(request.lastPassword(), request.newPassword());
    }


}
