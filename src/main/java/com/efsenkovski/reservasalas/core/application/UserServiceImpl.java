package com.efsenkovski.reservasalas.core.application;

import com.efsenkovski.reservasalas.core.domain.exception.ResourceNotFoundException;
import com.efsenkovski.reservasalas.core.domain.model.User;
import com.efsenkovski.reservasalas.core.domain.port.in.UserService;
import com.efsenkovski.reservasalas.core.domain.port.out.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(String name, String email) {
        return this.userRepository.saveUser(new User(name, email));
    }

    @Override
    public List<User> getAllUsers() {
        return this.userRepository.findAll();
    }

    @Override
    public User getUserById(UUID externalId) {
        return this.userRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + externalId));
    }
}
