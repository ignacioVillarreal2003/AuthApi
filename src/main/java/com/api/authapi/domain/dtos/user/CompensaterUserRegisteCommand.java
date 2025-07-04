package com.api.authapi.domain.dtos.user;

import java.util.UUID;

public record CompensaterUserRegisteCommand (
    UUID sagaId,
    String reason,
    Long userId
) {
}
