package com.api.authapi.infraestructure.persistence.repositories;

import com.api.authapi.domain.models.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = {"roles", "roles.role"})
    Optional<User> findByEmail(String email);
}
