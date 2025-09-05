package dev.ignacio.villarreal.authenticationapi.domain.dto.userRole;

import dev.ignacio.villarreal.authenticationapi.domain.dto.role.RoleResponse;
import dev.ignacio.villarreal.authenticationapi.domain.dto.user.UserResponse;
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
