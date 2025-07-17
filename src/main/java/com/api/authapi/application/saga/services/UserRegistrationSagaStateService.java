package com.api.authapi.application.saga.services;

import com.api.authapi.application.exceptions.SagaNotFoundException;
import com.api.authapi.domain.saga.step.UserRegistrationSagaStep;
import com.api.authapi.domain.saga.state.UserRegistrationSagaState;
import com.api.authapi.infrastructure.persistence.repositories.UserRegistrationSagaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserRegistrationSagaStateService {

    private final UserRegistrationSagaRepository registerSagaRepository;

    public UserRegistrationSagaState getOrStartSaga(UUID sagaId) {
        return registerSagaRepository.findById(sagaId)
                .orElseGet(() -> {
                    log.info("Creating new saga state for sagaId={}", sagaId);
                    return registerSagaRepository.save(new UserRegistrationSagaState(sagaId));
                });
    }

    public UserRegistrationSagaState getUserRegistrationSagaState(UUID sagaId) {
        log.debug("[UserRegistrationSagaStateService::getUserRegistrationSagaState] Getting saga state. sagaId={}", sagaId);
        return registerSagaRepository.findById(sagaId)
                .orElseThrow(() -> new SagaNotFoundException("Saga not found: " + sagaId));
    }

    public void startSaga(UUID sagaId) {
        log.info("[UserRegistrationSagaStateService::startSaga] Starting saga. sagaId={}", sagaId);
        if (!registerSagaRepository.existsById(sagaId)) {
            registerSagaRepository.save(new UserRegistrationSagaState(sagaId));
        }
    }

    public void createSaga(UUID sagaId,
                           Long userId) {
        log.info("[UserRegistrationSagaStateService::completeSaga] Marking saga as CREATED. sagaId={}", sagaId);
        registerSagaRepository.findById(sagaId).ifPresent(s -> {
            s.markStep(UserRegistrationSagaStep.CREATED);
            s.setUserId(userId);
            registerSagaRepository.save(s);
        });
    }

    public void completeSaga(UUID sagaId) {
        log.info("[UserRegistrationSagaStateService::completeSaga] Marking saga as COMPLETED. sagaId={}", sagaId);
        registerSagaRepository.findById(sagaId).ifPresent(s -> {
            s.markStep(UserRegistrationSagaStep.COMPLETED);
            registerSagaRepository.save(s);
        });
    }

    public void compensateSaga(UUID sagaId) {
        log.info("[UserRegistrationSagaStateService::compensateSaga] Marking saga as COMPENSATED. sagaId={}", sagaId);
        registerSagaRepository.findById(sagaId).ifPresent(s -> {
            s.markStep(UserRegistrationSagaStep.COMPENSATED);
            registerSagaRepository.save(s);
        });
    }

    public void failSaga(UUID sagaId) {
        log.info("[UserRegistrationSagaStateService::compensateSaga] Marking saga as FAIL. sagaId={}", sagaId);
        UserRegistrationSagaState state = registerSagaRepository.findById(sagaId).orElse(null);
        if (state == null) {
            state = new UserRegistrationSagaState(sagaId);
        }
        state.markStep(UserRegistrationSagaStep.FAILED);
        registerSagaRepository.save(state);
    }
}
