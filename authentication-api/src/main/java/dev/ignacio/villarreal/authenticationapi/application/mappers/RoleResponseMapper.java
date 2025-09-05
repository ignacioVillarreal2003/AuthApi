package dev.ignacio.villarreal.authenticationapi.application.mappers;

import dev.ignacio.villarreal.authenticationapi.domain.dto.role.RoleResponse;
import dev.ignacio.villarreal.authenticationapi.domain.model.Role;
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
