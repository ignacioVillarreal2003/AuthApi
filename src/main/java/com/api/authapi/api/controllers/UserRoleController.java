package com.api.authapi.api.controllers;

import com.api.authapi.application.services.userRole.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("api/v1/userRoles")
@RequiredArgsConstructor
public class UserRoleController {

    private final UserRoleService userRoleService;
}
