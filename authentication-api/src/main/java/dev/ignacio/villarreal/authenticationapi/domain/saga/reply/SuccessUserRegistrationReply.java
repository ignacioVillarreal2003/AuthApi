package dev.ignacio.villarreal.authenticationapi.domain.saga.reply;

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
public class SuccessUserRegistrationReply implements Serializable {
        private UUID sagaId;
        private String token;
        private String refreshToken;
}