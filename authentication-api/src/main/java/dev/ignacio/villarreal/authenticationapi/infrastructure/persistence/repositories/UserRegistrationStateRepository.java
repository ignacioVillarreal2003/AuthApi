package dev.ignacio.villarreal.authenticationapi.infrastructure.persistence.repositories;

import dev.ignacio.villarreal.authenticationapi.domain.saga.state.UserRegistrationState;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRegistrationStateRepository extends CrudRepository<UserRegistrationState, UUID> {

}
