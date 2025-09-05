package dev.ignacio.villarreal.authenticationapi.application.exceptions.notFound;

public class SagaNotFoundException extends NotFoundException {
    public SagaNotFoundException() {
        super(
                "SAGA_NOT_FOUND",
                "Saga not found."
        );
    }
}