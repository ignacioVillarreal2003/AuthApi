package dev.ignacio.villarreal.authenticationapi.application.services.role;

import dev.ignacio.villarreal.authenticationapi.application.exceptions.notFound.RoleNotFoundException;
import dev.ignacio.villarreal.authenticationapi.domain.model.Role;
import dev.ignacio.villarreal.authenticationapi.infrastructure.persistence.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleRetrievalService {

    private final RoleRepository roleRepository;

    public Role getByName(String name) {
        log.debug("Retrieving role '{}'", name);

        return roleRepository.findByName(name)
                .orElseThrow(() -> {
                    log.warn("Role '{}' not found", name);
                    return new RoleNotFoundException();
                });
    }
}
