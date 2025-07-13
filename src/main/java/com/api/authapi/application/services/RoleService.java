package com.api.authapi.application.services;

import com.api.authapi.application.exceptions.RoleNotFoundException;
import com.api.authapi.domain.models.Role;
import com.api.authapi.infraestructure.persistence.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public Role getRoleByName(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(RoleNotFoundException::new);
    }
}
