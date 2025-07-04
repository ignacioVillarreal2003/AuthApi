package com.api.authapi.domain.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegisterReply implements Serializable {
        private UUID sagaId;
        private Long userId;
        private boolean success;
        private String token;
        private String refreshToken;
        private String errorMessage;
}