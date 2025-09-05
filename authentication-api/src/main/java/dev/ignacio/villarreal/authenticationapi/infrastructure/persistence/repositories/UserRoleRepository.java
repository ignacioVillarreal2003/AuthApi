package dev.ignacio.villarreal.authenticationapi.infrastructure.persistence.repositories;

import dev.ignacio.villarreal.authenticationapi.domain.model.Role;
import dev.ignacio.villarreal.authenticationapi.domain.model.User;
import dev.ignacio.villarreal.authenticationapi.domain.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    boolean existsByUserAndRole(User user, Role role);

    Optional<UserRole> findByUserAndRole(User user, Role role);
}
