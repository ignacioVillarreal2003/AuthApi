package com.api.authapi.application.mappers;

import com.api.authapi.domain.dto.user.UserResponse;
import com.api.authapi.domain.model.User;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class UserResponseMapper implements Function<User, UserResponse> {

    @Override
    public UserResponse apply(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .roles(user.getRoles()
                        .stream()
                        .map(role ->
                                role.getRole().getName())
                        .toList())
                .build();
    }
}
