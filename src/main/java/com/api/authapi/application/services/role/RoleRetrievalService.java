package com.api.authapi.application.services.role;

import com.api.authapi.application.exceptions.RoleNotFoundException;
import com.api.authapi.domain.model.Role;
import com.api.authapi.infrastructure.persistence.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleRetrievalService {

    private final RoleRepository roleRepository;

    public Role getByName(String name) {
        log.debug("[RoleRetrievalService::findByName] - Retrieving role '{}'", name);

        return roleRepository.findByName(name)
                .orElseThrow(() -> {
                    log.warn("[RoleRetrievalService::findByName] - Role '{}' not found", name);
                    return new RoleNotFoundException();
                });
    }
}
