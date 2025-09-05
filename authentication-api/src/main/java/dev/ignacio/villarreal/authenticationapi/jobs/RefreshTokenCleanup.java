package dev.ignacio.villarreal.authenticationapi.jobs;

import dev.ignacio.villarreal.authenticationapi.infrastructure.persistence.repositories.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenCleanup {

    private final RefreshTokenRepository repository;

    /*@Scheduled(cron = "0 0 3 * * *")
    public void purgeExpired() {
        repository.deleteByExpiresAtBefore(Instant.now());
    }*/
}
