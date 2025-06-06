package com.api.authapi.domain.dtos.userRole;

import com.api.authapi.domain.dtos.user.UserResponse;
import com.api.authapi.domain.enums.Role;
import lombok.Builder;

import java.io.Serializable;

@Builder
public class UserRoleResponse implements Serializable {
    private Long id;
    private Role role;
    private UserResponse user;
}
