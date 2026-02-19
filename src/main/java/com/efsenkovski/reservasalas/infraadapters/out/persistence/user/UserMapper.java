package com.efsenkovski.reservasalas.infraadapters.out.persistence.user;

import com.efsenkovski.reservasalas.core.domain.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toUser(UserEntity entity) {
        return new User(
                entity.getExternalId(),
                entity.getName(),
                entity.getEmail(),
                entity.getCreatedAt()
        );
    }

    public UserEntity toEntity(User user) {
        return new UserEntity(
                user.getExternalId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }
}
