package com.api.authapi.domain.dtos.user;

import lombok.Builder;

import java.io.Serializable;
import java.util.UUID;

@Builder
public class UserRegisterReply implements Serializable {
        private UUID sagaId;
        private Long userId;
        private boolean success;
        private String token;
        private String refreshToken;
        private String errorMessage;
}