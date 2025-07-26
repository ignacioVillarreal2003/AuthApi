package com.api.authapi.application.mappers;

import com.api.authapi.domain.dto.user.UserResponse;
import com.api.authapi.domain.model.User;
import com.api.authapi.domain.model.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class UserResponseMapper implements Function<User, UserResponse> {

    private final RoleResponseMapper roleResponseMapper;

    @Override
    public UserResponse apply(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .roles(user.getUserRoles()
                        .stream()
                        .map(UserRole::getRole)
                        .toList()
                        .stream()
                        .map(roleResponseMapper)
                        .toList())
                .build();
    }
}
