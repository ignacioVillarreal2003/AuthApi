package com.api.authapi.domain.dto.userRole;

import com.api.authapi.domain.dto.role.RoleResponse;
import com.api.authapi.domain.dto.user.UserResponse;
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
