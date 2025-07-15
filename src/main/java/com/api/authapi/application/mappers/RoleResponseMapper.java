package com.api.authapi.application.mappers;

import com.api.authapi.domain.dto.role.RoleResponse;
import com.api.authapi.domain.model.Role;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class RoleResponseMapper implements Function<Role, RoleResponse> {

    @Override
    public RoleResponse apply(Role role) {
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .build();
    }
}
