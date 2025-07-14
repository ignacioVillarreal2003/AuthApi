package com.api.authapi.domain.models;

import com.api.authapi.domain.constants.RegisterSagaStep;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.Instant;
import java.util.UUID;

@RedisHash(value = "user_register_saga", timeToLive = 7200)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserRegisterSaga {

    @Id
    private UUID sagaId;

    private Long userId;

    private RegisterSagaStep step;

    private Instant createdAt;

    private Instant updatedAt;

    public UserRegisterSaga(UUID sagaId) {
        this.sagaId = sagaId;
        this.step = RegisterSagaStep.PENDING;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public void markStep(RegisterSagaStep newStep) {
        this.step = newStep;
        this.updatedAt = Instant.now();
    }
}
