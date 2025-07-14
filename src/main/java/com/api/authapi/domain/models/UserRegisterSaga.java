package com.api.authapi.domain.models;

import com.api.authapi.domain.constants.SagaStep;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

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

    private SagaStep step;

    private Instant createdAt;

    private Instant updatedAt;

    @TimeToLive
    private Long ttl;

    public UserRegisterSaga(UUID sagaId) {
        this.sagaId = sagaId;
        this.step = SagaStep.PENDING;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public void markStep(SagaStep newStep) {
        this.step = newStep;
        this.updatedAt = Instant.now();
    }
}
