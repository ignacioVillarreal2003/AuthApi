package com.api.authapi.infrastructure.persistence.repositories;

import com.api.authapi.domain.saga.state.UserRegistrationState;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRegistrationStateRepository extends CrudRepository<UserRegistrationState, UUID> {

}
