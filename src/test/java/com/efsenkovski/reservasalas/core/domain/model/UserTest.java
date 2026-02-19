package com.efsenkovski.reservasalas.core.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User domain model")
class UserTest {

    @Nested
    @DisplayName("When creating a user with the business constructor")
    class BusinessConstructor {

        @Test
        @DisplayName("should set all fields correctly")
        void shouldSetAllFieldsCorrectly() {
            var user = new User("John Doe", "john@example.com");

            assertNotNull(user.getExternalId());
            assertEquals("John Doe", user.getName());
            assertEquals("john@example.com", user.getEmail());
            assertNotNull(user.getCreatedAt());
        }
    }

    @Nested
    @DisplayName("When using the full constructor (persistence reconstruction)")
    class FullConstructor {

        @Test
        @DisplayName("should accept all provided values")
        void shouldAcceptAllValues() {
            var externalId = UUID.randomUUID();
            var createdAt = LocalDateTime.of(2026, 1, 15, 10, 0);

            var user = new User(externalId, "Jane Doe", "jane@example.com", createdAt);

            assertEquals(externalId, user.getExternalId());
            assertEquals("Jane Doe", user.getName());
            assertEquals("jane@example.com", user.getEmail());
            assertEquals(createdAt, user.getCreatedAt());
        }
    }
}
