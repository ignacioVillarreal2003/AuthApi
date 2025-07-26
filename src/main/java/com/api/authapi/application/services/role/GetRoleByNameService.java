package com.api.authapi.application.services.role;

import com.api.authapi.application.exceptions.RoleNotFoundException;
import com.api.authapi.domain.model.Role;
import com.api.authapi.infrastructure.persistence.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetRoleByNameService {

    private final RoleRepository roleRepository;

    public Role execute(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(RoleNotFoundException::new);
    }
}
