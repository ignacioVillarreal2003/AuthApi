package dev.ignacio.villarreal.authenticationapi.infrastructure.persistence.repositories;

import dev.ignacio.villarreal.authenticationapi.domain.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {
    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findAllByUser_Id(Long userId);
}
