package dev.ignacio.villarreal.authenticationapi.infrastructure.persistence.repositories;

import dev.ignacio.villarreal.authenticationapi.domain.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = {"userRoles", "userRoles.role"})
    Optional<User> findByEmail(String email);

    Optional<User> findByActivationToken(UUID activationToken);
}
