package com.api.authapi.domain.saga.reply;

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
public class AwaitingVerificationUserRegistrationReply implements Serializable {
    private UUID sagaId;
}
