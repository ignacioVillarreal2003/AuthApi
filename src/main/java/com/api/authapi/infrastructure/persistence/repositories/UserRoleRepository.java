package com.api.authapi.infrastructure.persistence.repositories;

import com.api.authapi.domain.model.Role;
import com.api.authapi.domain.model.User;
import com.api.authapi.domain.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    boolean existsByUserAndRole(User user, Role role);

    Optional<UserRole> findByUserAndRole(User user, Role role);
}
