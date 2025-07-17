package com.api.authapi.application.saga.services;

import com.api.authapi.application.exceptions.SagaNotFoundException;
import com.api.authapi.domain.saga.state.UserRegistrationState;
import com.api.authapi.domain.saga.step.UserRegistrationStep;
import com.api.authapi.infrastructure.persistence.repositories.UserRegistrationSagaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserRegistrationStateService {

    private final UserRegistrationSagaRepository repository;

    public UserRegistrationState getOrStartSaga(UUID sagaId) {
        log.debug("[UserRegistrationSagaStateService::getOrStartSaga] Retrieving or starting new saga. sagaId={}", sagaId);
        return repository.findById(sagaId)
                .orElseGet(() -> {
                    log.info("[UserRegistrationSagaStateService::getOrStartSaga] Creating new saga state. sagaId={}", sagaId);
                    return repository.save(new UserRegistrationState(sagaId));
                });
    }

    public UserRegistrationState getSagaState(UUID sagaId) {
        log.debug("[UserRegistrationSagaStateService::getSagaState] Getting saga state. sagaId={}", sagaId);
        return repository.findById(sagaId)
                .orElseThrow(SagaNotFoundException::new);
    }

    public void markCreated(UUID sagaId,
                           Long userId) {
        log.info("[UserRegistrationSagaStateService::markCreated] Marking saga as CREATED. sagaId={}, userId={}", sagaId, userId);
        UserRegistrationState state = repository.findById(sagaId)
                .orElseThrow(SagaNotFoundException::new);
        state.markStep(UserRegistrationStep.CREATED);
        state.setUserId(userId);
        repository.save(state);
    }

    public void markCompleted(UUID sagaId) {
        log.info("[UserRegistrationSagaStateService::markCompleted] Marking saga as COMPLETED. sagaId={}", sagaId);
        UserRegistrationState state = repository.findById(sagaId)
                .orElseThrow(SagaNotFoundException::new);
        state.markStep(UserRegistrationStep.COMPLETED);
        repository.save(state);
    }

    public void markCompensated(UUID sagaId) {
        log.info("[UserRegistrationSagaStateService::markCompensated] Marking saga as COMPENSATED. sagaId={}", sagaId);
        UserRegistrationState state = repository.findById(sagaId)
                .orElseThrow(SagaNotFoundException::new);
        state.markStep(UserRegistrationStep.COMPENSATED);
        repository.save(state);
    }

    public void markFailed(UUID sagaId) {
        log.info("[UserRegistrationSagaStateService::markFailed] Marking saga as FAILED. sagaId={}", sagaId);
        UserRegistrationState state = repository.findById(sagaId)
                .orElseThrow(SagaNotFoundException::new);
        state.markStep(UserRegistrationStep.FAILED);
        repository.save(state);
    }
}
