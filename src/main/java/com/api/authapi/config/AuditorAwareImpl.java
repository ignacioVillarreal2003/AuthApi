package com.api.authapi.config;

import com.api.authapi.config.authentication.AuthenticationUserProvider;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("auditorAware")
@RequiredArgsConstructor
public class AuditorAwareImpl implements AuditorAware<String> {

    private final AuthenticationUserProvider authenticationUserProvider;

    @Override
    public @NotNull Optional<String> getCurrentAuditor() {
        String username = authenticationUserProvider.getUser().getUsername();
        if (username != null) {
            return Optional.of(username);
        }
        return Optional.empty();
    }
}
