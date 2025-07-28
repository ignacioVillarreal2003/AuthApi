package com.api.authapi.application.saga.services;

import com.api.authapi.application.exceptions.notFound.SagaNotFoundException;
import com.api.authapi.domain.saga.state.UserRegistrationState;
import com.api.authapi.domain.saga.step.UserRegistrationStep;
import com.api.authapi.infrastructure.persistence.repositories.UserRegistrationStateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserRegistrationStateService {

    private final UserRegistrationStateRepository repository;

    public UserRegistrationState getOrStartSaga(UUID sagaId, String email, List<String> roles) {
        return repository.findById(sagaId)
                .orElseGet(() -> {
                    return repository.save(new UserRegistrationState(sagaId, email, roles));
                });
    }

    public UserRegistrationState getSagaState(UUID sagaId) {
        return repository.findById(sagaId)
                .orElseThrow(SagaNotFoundException::new);
    }

    public void markCreated(UUID sagaId,
                            Long userId,
                            String token,
                            String refreshToken,
                            boolean isNewUser) {
        UserRegistrationState state = repository.findById(sagaId)
                .orElseThrow(SagaNotFoundException::new);
        state.markStep(UserRegistrationStep.USER_CREATED);
        state.setUserId(userId);
        state.setToken(token);
        state.setRefreshToken(refreshToken);
        state.setNewUser(isNewUser);
        repository.save(state);
    }

    public void markPendingVerification(UUID sagaId) {
        UserRegistrationState state = repository.findById(sagaId)
                .orElseThrow(SagaNotFoundException::new);
        state.markStep(UserRegistrationStep.PENDING_VERIFICATION);
        repository.save(state);
    }

    public void markCompleted(UUID sagaId) {
        UserRegistrationState state = repository.findById(sagaId)
                .orElseThrow(SagaNotFoundException::new);
        state.markStep(UserRegistrationStep.COMPLETED);
        repository.save(state);
    }

    public void markCompensated(UUID sagaId) {
        UserRegistrationState state = repository.findById(sagaId)
                .orElseThrow(SagaNotFoundException::new);
        state.markStep(UserRegistrationStep.COMPENSATED);
        repository.save(state);
    }

    public void markFailed(UUID sagaId) {
        UserRegistrationState state = repository.findById(sagaId)
                .orElseThrow(SagaNotFoundException::new);
        state.markStep(UserRegistrationStep.FAILED);
        repository.save(state);
    }
}
