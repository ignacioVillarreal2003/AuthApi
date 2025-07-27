package com.api.authapi.domain.saga.state;

import com.api.authapi.domain.saga.step.UserRegistrationStep;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RedisHash(value = "user_registration_state_auth_api", timeToLive = 7200)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserRegistrationState {

    @Id
    private UUID sagaId;

    private boolean isNewUser;

    private String email;

    private List<String> roles;

    private Long userId;

    private String token;

    private String refreshToken;

    private UserRegistrationStep step;

    private Instant createdAt;

    private Instant updatedAt;

    public UserRegistrationState(UUID sagaId, String email, List<String> roles) {
        this.sagaId = sagaId;
        this.email = email;
        this.roles = roles;
        this.step = UserRegistrationStep.STARTED;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public void markStep(UserRegistrationStep newStep) {
        this.step = newStep;
        this.updatedAt = Instant.now();
    }
}
