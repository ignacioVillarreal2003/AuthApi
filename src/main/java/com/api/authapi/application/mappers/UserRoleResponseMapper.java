package com.api.authapi.application.mappers;

import com.api.authapi.domain.dtos.userRole.UserRoleResponse;
import com.api.authapi.domain.models.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class UserRoleResponseMapper implements Function<UserRole, UserRoleResponse> {

    private final UserResponseMapper userResponseMapper;

    @Override
    public UserRoleResponse apply(UserRole userRole) {
        return UserRoleResponse.builder()
                .id(userRole.getId())
                .role(userRole.getRole())
                .user(userResponseMapper.apply(userRole.getUser()))
                .build();
    }
}
