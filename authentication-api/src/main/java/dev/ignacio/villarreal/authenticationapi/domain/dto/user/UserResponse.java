package dev.ignacio.villarreal.authenticationapi.domain.dto.user;

import dev.ignacio.villarreal.authenticationapi.domain.dto.role.RoleResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse implements Serializable {
    private Long id;
    private String email;
    private List<RoleResponse> roles;
}