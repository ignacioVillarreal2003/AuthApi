package com.api.authapi.infraestructure.persistence.repositories;

import com.api.authapi.domain.models.UserRegisterSaga;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRegisterSagaRepository extends CrudRepository<UserRegisterSaga, UUID> {
}
