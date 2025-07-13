package com.api.authapi.application.mappers;

import com.api.authapi.domain.dtos.role.RoleResponse;
import com.api.authapi.domain.models.Role;
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
