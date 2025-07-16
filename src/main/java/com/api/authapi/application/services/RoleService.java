package com.api.authapi.application.services;

import com.api.authapi.application.exceptions.RoleNotFoundException;
import com.api.authapi.domain.model.Role;
import com.api.authapi.infrastructure.persistence.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleService {

    private final RoleRepository roleRepository;

    public Role getRoleByName(String roleName) {
        log.info("[RoleService::getRoleByName] Fetching role with name: {}", roleName);
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> {
                    log.warn("[RoleService::getRoleByName] Role not found: {}", roleName);
                    return new RoleNotFoundException();
                });
    }
}
