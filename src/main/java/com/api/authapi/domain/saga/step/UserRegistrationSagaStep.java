package com.api.authapi.domain.saga.step;

public enum UserRegistrationSagaStep {
    PENDING,
    USER_CREATED,
    COMPLETED,
    COMPENSATED
}