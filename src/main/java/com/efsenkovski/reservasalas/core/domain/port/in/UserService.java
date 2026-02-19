package com.efsenkovski.reservasalas.core.domain.port.in;

import com.efsenkovski.reservasalas.core.domain.model.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User createUser(String name, String email);
    List<User> getAllUsers();
    User getUserById(UUID externalId);
}
