package com.api.authapi.jobs;

import com.api.authapi.infrastructure.persistence.repositories.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RefreshTokenCleanup {

    private final RefreshTokenRepository repository;

    /*@Scheduled(cron = "0 0 3 * * *")
    public void purgeExpired() {
        repository.deleteByExpiresAtBefore(Instant.now());
    }*/
}
