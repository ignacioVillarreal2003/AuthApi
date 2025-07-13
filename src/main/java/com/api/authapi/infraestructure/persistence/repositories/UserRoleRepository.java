package com.api.authapi.infraestructure.persistence.repositories;

import com.api.authapi.domain.models.Role;
import com.api.authapi.domain.models.User;
import com.api.authapi.domain.models.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    boolean existsByUserAndRole(User user, Role role);
}
