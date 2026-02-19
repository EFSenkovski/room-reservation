package com.efsenkovski.reservasalas.core.domain.port.out;

import com.efsenkovski.reservasalas.core.domain.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User saveUser(User user);
    List<User> findAll();
    Optional<User> findByExternalId(UUID externalId);
}
