package com.efsenkovski.reservasalas.infraadapters.out.persistence.user;

import com.efsenkovski.reservasalas.core.domain.exception.DomainException;
import com.efsenkovski.reservasalas.core.domain.model.User;
import com.efsenkovski.reservasalas.core.domain.port.out.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;
    private final UserMapper mapper;

    public UserRepositoryImpl(UserJpaRepository userJpaRepository, UserMapper mapper) {
        this.userJpaRepository = userJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public User saveUser(User user) {
        if (this.userJpaRepository.existsByEmail(user.getEmail())) {
            throw new DomainException("User with email " + user.getEmail() + " already exists");
        }
        var savedUser = this.userJpaRepository.save(mapper.toEntity(user));
        return mapper.toUser(savedUser);
    }

    @Override
    public List<User> findAll() {
        return this.userJpaRepository.findAll().stream()
                .map(mapper::toUser)
                .toList();
    }

    @Override
    public Optional<User> findByExternalId(UUID externalId) {
        return this.userJpaRepository.findByExternalId(externalId)
                .map(mapper::toUser);
    }
}
