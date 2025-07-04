package com.api.authapi.infraestructure.persistence.repositories;

import com.api.authapi.domain.models.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndEnabledTrue(String email);

    Optional<User> findByIdAndEnabledTrue(Long id);

    @Query("SELECT u FROM User u WHERE u.id = :id")
    @EntityGraph(attributePaths = {"roles"})
    Optional<User> findByIdWithRoles(@Param("id") Long id);
}
