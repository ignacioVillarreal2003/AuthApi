package dev.ignacio.villarreal.authenticationapi.application.services.user;

import dev.ignacio.villarreal.authenticationapi.config.annotations.RequireActiveAccount;
import dev.ignacio.villarreal.authenticationapi.domain.dto.user.ChangePasswordRequest;
import dev.ignacio.villarreal.authenticationapi.domain.dto.user.ReactiveAccountRequest;
import dev.ignacio.villarreal.authenticationapi.domain.dto.user.RequestAccountReactivationRequest;
import dev.ignacio.villarreal.authenticationapi.domain.dto.user.VerifyAccountRequest;
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
