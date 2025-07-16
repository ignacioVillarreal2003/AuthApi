package com.api.authapi.application.saga.services;

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

    public UserRegistrationSagaState getUserRegistrationSagaState(UUID sagaId) {
        log.debug("[UserRegistrationSagaStateService::getUserRegistrationSagaState] Getting saga state. sagaId={}", sagaId);
        return registerSagaRepository.findById(sagaId).orElseThrow();
    }

    public void startSaga(UUID sagaId) {
        log.info("[UserRegistrationSagaStateService::startSaga] Starting saga. sagaId={}", sagaId);
        if (!registerSagaRepository.existsById(sagaId)) {
            registerSagaRepository.save(new UserRegistrationSagaState(sagaId));
        }
    }

    public void markUserCreated(UUID sagaId, Long userId) {
        log.info("[UserRegistrationSagaStateService::markUserCreated] Marking USER_CREATED. sagaId={}, userId={}", sagaId, userId);
        registerSagaRepository.findById(sagaId).ifPresent(s -> {
            s.setUserId(userId);
            s.markStep(UserRegistrationSagaStep.USER_CREATED);
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

    public boolean isStepCompleted(UUID sagaId, UserRegistrationSagaStep step) {
        boolean completed = registerSagaRepository.findById(sagaId)
                .map(s -> s.getStep().ordinal() >= step.ordinal())
                .orElse(false);
        log.debug("[UserRegistrationSagaStateService::isStepCompleted] sagaId={}, step={}, completed={}", sagaId, step, completed);
        return completed;
    }
}
