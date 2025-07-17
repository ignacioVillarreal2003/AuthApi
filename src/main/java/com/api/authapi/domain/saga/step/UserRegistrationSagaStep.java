package com.api.authapi.domain.saga.step;

public enum UserRegistrationSagaStep {
    STARTED,
    CREATED,
    COMPLETED,
    COMPENSATED,
    FAILED
}