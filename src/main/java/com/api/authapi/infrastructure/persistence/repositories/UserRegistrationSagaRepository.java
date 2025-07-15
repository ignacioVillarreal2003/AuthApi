package com.api.authapi.infrastructure.persistence.repositories;

import com.api.authapi.domain.saga.state.UserRegistrationSagaState;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRegistrationSagaRepository extends CrudRepository<UserRegistrationSagaState, UUID> {
}
