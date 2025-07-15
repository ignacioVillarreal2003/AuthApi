package com.api.authapi.startup;

import com.api.authapi.domain.model.Role;
import com.api.authapi.domain.model.User;
import com.api.authapi.domain.model.UserRole;
import com.api.authapi.infrastructure.persistence.repositories.RoleRepository;
import com.api.authapi.infrastructure.persistence.repositories.UserRepository;
import com.api.authapi.infrastructure.persistence.repositories.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataLoader implements ApplicationRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        roleRepository.deleteAll();
        userRepository.deleteAll();
        userRoleRepository.deleteAll();

        Role roleAdmin = Role.builder()
                .name("ROLE_ADMIN")
                .build();
        Role roleUser1 = Role.builder()
                .name("ROLE_RESCUE_ME_USER")
                .build();

        roleRepository.saveAll(List.of(roleAdmin, roleUser1));

        User userAdmin = User.builder()
                .email("ignaciovillarreal20031231@gmail.com")
                .password(passwordEncoder.encode("12345678"))
                .build();

        userRepository.save(userAdmin);

        UserRole userRole1 = UserRole.builder()
                .user(userAdmin)
                .role(roleAdmin)
                .build();
        UserRole userRole2 = UserRole.builder()
                .user(userAdmin)
                .role(roleUser1)
                .build();

        userRoleRepository.saveAll(List.of(userRole1, userRole2));
    }
}
