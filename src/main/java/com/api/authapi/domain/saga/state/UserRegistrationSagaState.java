package com.api.authapi.domain.saga.state;

import com.api.authapi.domain.saga.step.UserRegistrationSagaStep;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.Instant;
import java.util.UUID;

@RedisHash(value = "user_registration_saga_auth_api", timeToLive = 7200)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserRegistrationSagaState {

    @Id
    private UUID sagaId;

    private Long userId;

    private UserRegistrationSagaStep step;

    private Instant createdAt;

    private Instant updatedAt;

    public UserRegistrationSagaState(UUID sagaId) {
        this.sagaId = sagaId;
        this.step = UserRegistrationSagaStep.PENDING;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public void markStep(UserRegistrationSagaStep newStep) {
        this.step = newStep;
        this.updatedAt = Instant.now();
    }
}
