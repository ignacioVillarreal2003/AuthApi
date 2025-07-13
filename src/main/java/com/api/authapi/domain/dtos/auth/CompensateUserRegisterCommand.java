package com.api.authapi.domain.dtos.auth;

import java.util.UUID;

public record CompensateUserRegisterCommand(
    UUID sagaId,
    String reason
) {
}
