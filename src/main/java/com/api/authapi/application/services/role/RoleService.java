package com.api.authapi.application.services.role;

import com.api.authapi.domain.model.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleService {

    private final GetRoleByNameService getRoleByNameService;

    @Transactional
    public Role getRoleByName(String name) {
        return getRoleByNameService.execute(name);
    }
}
