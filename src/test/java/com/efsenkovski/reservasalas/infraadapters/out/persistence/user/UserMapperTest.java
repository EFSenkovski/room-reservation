package com.efsenkovski.reservasalas.infraadapters.out.persistence.user;

import com.efsenkovski.reservasalas.core.domain.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserMapper")
class UserMapperTest {

    private final UserMapper mapper = new UserMapper();

    @Nested
    @DisplayName("toUser")
    class ToUser {

        @Test
        @DisplayName("should map all fields from entity to domain model")
        void shouldMapAllFields() {
            var entity = new UserEntity(UUID.randomUUID(), "Alice", "alice@example.com",
                    LocalDateTime.of(2026, 1, 15, 10, 0));

            var user = mapper.toUser(entity);

            assertEquals(entity.getExternalId(), user.getExternalId());
            assertEquals(entity.getName(), user.getName());
            assertEquals(entity.getEmail(), user.getEmail());
            assertEquals(entity.getCreatedAt(), user.getCreatedAt());
        }
    }

    @Nested
    @DisplayName("toEntity")
    class ToEntity {

        @Test
        @DisplayName("should map all fields from domain model to entity")
        void shouldMapAllFields() {
            var user = new User(UUID.randomUUID(), "Bob", "bob@example.com",
                    LocalDateTime.of(2026, 2, 10, 14, 30));

            var entity = mapper.toEntity(user);

            assertEquals(user.getExternalId(), entity.getExternalId());
            assertEquals(user.getName(), entity.getName());
            assertEquals(user.getEmail(), entity.getEmail());
            assertEquals(user.getCreatedAt(), entity.getCreatedAt());
        }
    }
}
