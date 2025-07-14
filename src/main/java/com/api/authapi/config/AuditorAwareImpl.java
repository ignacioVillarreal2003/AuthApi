package com.api.authapi.config;

import com.api.authapi.config.authentication.AuthenticationUserProvider;
import com.api.authapi.domain.models.User;
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
        User user = authenticationUserProvider.getUser();
        if (user != null) {
            String username = user.getUsername();
            return Optional.of(username);
        }
        return Optional.of("system");
    }
}
