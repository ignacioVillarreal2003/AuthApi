package com.api.authapi.application.exceptions;

public class SagaNotFoundException extends NotFoundException {
    public SagaNotFoundException() {
        super(
                "SAGA_NOT_FOUND",
                "Saga not found."
        );
    }
}