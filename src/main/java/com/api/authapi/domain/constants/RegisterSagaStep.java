package com.api.authapi.domain.constants;

public enum RegisterSagaStep {
    PENDING,
    USER_CREATED,
    COMPLETED,
    COMPENSATED
}