package com.api.authapi.application.services;

import com.api.authapi.domain.constants.RegisterSagaStep;
import com.api.authapi.domain.models.UserRegisterSaga;
import com.api.authapi.infraestructure.persistence.repositories.UserRegisterSagaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserRegisterSagaService {

    private final UserRegisterSagaRepository registerSagaRepository;

    public void startSaga(UUID sagaId) {
        if (!registerSagaRepository.existsById(sagaId)) {
            registerSagaRepository.save(new UserRegisterSaga(sagaId));
        }
    }

    public void markUserCreated(UUID sagaId, Long userId) {
        registerSagaRepository.findById(sagaId).ifPresent(s -> {
            s.setUserId(userId);
            s.markStep(RegisterSagaStep.USER_CREATED);
            registerSagaRepository.save(s);
        });
    }

    public boolean isStepCompleted(UUID sagaId, RegisterSagaStep step) {
        return registerSagaRepository.findById(sagaId)
                .map(s -> s.getStep().ordinal() >= step.ordinal())
                .orElse(false);
    }

    public Optional<Long> getUserId(UUID sagaId) {
        return registerSagaRepository.findById(sagaId).map(UserRegisterSaga::getUserId);
    }

    public void completeSaga(UUID sagaId) {
        registerSagaRepository.findById(sagaId).ifPresent(s -> {
            s.markStep(RegisterSagaStep.COMPLETED);
            registerSagaRepository.save(s);
        });
    }

    public void compensateSaga(UUID sagaId) {
        registerSagaRepository.findById(sagaId).ifPresent(s -> {
            s.markStep(RegisterSagaStep.COMPENSATED);
            registerSagaRepository.save(s);
        });
    }
}
