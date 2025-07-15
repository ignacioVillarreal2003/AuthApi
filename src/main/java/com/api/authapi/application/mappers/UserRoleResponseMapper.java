package com.api.authapi.application.mappers;

import com.api.authapi.domain.dto.userRole.UserRoleResponse;
import com.api.authapi.domain.model.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class UserRoleResponseMapper implements Function<UserRole, UserRoleResponse> {

    private final UserResponseMapper userResponseMapper;
    private final RoleResponseMapper roleResponseMapper;

    @Override
    public UserRoleResponse apply(UserRole userRole) {
        return UserRoleResponse.builder()
                .id(userRole.getId())
                .role(roleResponseMapper.apply(userRole.getRole()))
                .user(userResponseMapper.apply(userRole.getUser()))
                .build();
    }
}
