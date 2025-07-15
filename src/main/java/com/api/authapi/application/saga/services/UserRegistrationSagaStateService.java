package com.api.authapi.application.saga.services;

import com.api.authapi.domain.saga.step.UserRegistrationSagaStep;
import com.api.authapi.domain.saga.state.UserRegistrationSagaState;
import com.api.authapi.infrastructure.persistence.repositories.UserRegistrationSagaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserRegistrationSagaStateService {

    private final UserRegistrationSagaRepository registerSagaRepository;

    public UserRegistrationSagaState getUserRegistrationSagaState(UUID sagaId) {
        return registerSagaRepository.findById(sagaId).orElseThrow();
    }

    public void startSaga(UUID sagaId) {
        if (!registerSagaRepository.existsById(sagaId)) {
            registerSagaRepository.save(new UserRegistrationSagaState(sagaId));
        }
    }

    public void markUserCreated(UUID sagaId, Long userId) {
        registerSagaRepository.findById(sagaId).ifPresent(s -> {
            s.setUserId(userId);
            s.markStep(UserRegistrationSagaStep.USER_CREATED);
            registerSagaRepository.save(s);
        });
    }

    public void completeSaga(UUID sagaId) {
        registerSagaRepository.findById(sagaId).ifPresent(s -> {
            s.markStep(UserRegistrationSagaStep.COMPLETED);
            registerSagaRepository.save(s);
        });
    }

    public void compensateSaga(UUID sagaId) {
        registerSagaRepository.findById(sagaId).ifPresent(s -> {
            s.markStep(UserRegistrationSagaStep.COMPENSATED);
            registerSagaRepository.save(s);
        });
    }

    public boolean isStepCompleted(UUID sagaId, UserRegistrationSagaStep step) {
        return registerSagaRepository.findById(sagaId)
                .map(s -> s.getStep().ordinal() >= step.ordinal())
                .orElse(false);
    }
}
