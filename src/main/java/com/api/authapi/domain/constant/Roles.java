package com.api.authapi.domain.constant;

import java.util.List;

public class Roles {

    public static List<String> getRoles() {
        return List.of(
                Role.ROLE_ADMIN.toString(),
                Role.ROLE_RESCUE_ME_USER.toString());
    }
}
