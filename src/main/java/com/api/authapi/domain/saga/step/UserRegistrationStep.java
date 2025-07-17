package com.api.authapi.domain.saga.step;

public enum UserRegistrationStep {
    STARTED,
    CREATED,
    COMPLETED,
    COMPENSATED,
    FAILED
}