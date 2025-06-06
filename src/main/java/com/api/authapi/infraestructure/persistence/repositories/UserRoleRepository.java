package com.api.authapi.infraestructure.persistence.repositories;

import com.api.authapi.domain.enums.Role;
import com.api.authapi.domain.models.User;
import com.api.authapi.domain.models.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    Optional<UserRole> findByUserAndRole(User user, Role role);

    List<UserRole> findAllByUser(User user);
}
