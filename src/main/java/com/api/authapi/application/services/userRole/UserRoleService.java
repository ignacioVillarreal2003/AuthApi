package com.api.authapi.application.services.userRole;

import com.api.authapi.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserRoleService {

    private final AssignRoleService assignRoleService;

    @Transactional
    public void assignRole(User user, String roleName) {
        assignRoleService.execute(user, roleName);
    }
}
