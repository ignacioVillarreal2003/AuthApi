package com.api.authapi.application.exceptions;

public class SagaNotFoundException extends RuntimeException {
    public SagaNotFoundException() {
        super("Saga not found");
    }
}
