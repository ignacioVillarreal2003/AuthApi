package dev.ignacio.villarreal.authenticationapi.domain.saga.step;

public enum UserRegistrationStep {
    STARTED,
    USER_CREATED,
    PENDING_VERIFICATION,
    COMPLETED,
    COMPENSATED,
    FAILED
}