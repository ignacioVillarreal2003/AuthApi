package com.api.authapi.domain.dtos.userRole;

import com.api.authapi.domain.dtos.role.RoleResponse;
import com.api.authapi.domain.dtos.user.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRoleResponse implements Serializable {
    private Long id;
    private RoleResponse role;
    private UserResponse user;
}
