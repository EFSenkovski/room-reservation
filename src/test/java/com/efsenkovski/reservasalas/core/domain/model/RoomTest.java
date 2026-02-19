package com.efsenkovski.reservasalas.core.domain.model;

import com.efsenkovski.reservasalas.core.domain.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Room domain model")
class RoomTest {

    @Nested
    @DisplayName("When creating a room with the business constructor")
    class BusinessConstructor {

        @Test
        @DisplayName("should set all fields correctly for valid input")
        void shouldSetAllFieldsCorrectly() {
            var room = new Room("Conference Room", 10L);

            assertNotNull(room.getExternalId());
            assertEquals("Conference Room", room.getName());
            assertEquals(10L, room.getCapacity());
            assertTrue(room.isActive());
        }

        @Test
        @DisplayName("should reject capacity less than or equal to zero")
        void shouldRejectNegativeCapacity() {
            DomainException exception = assertThrows(
                    DomainException.class,
                    () -> new Room("Room", -1L)
            );

            assertEquals("Capacity must be greater than zero", exception.getMessage());
        }

        @Test
        @DisplayName("should reject capacity equal to zero")
        void shouldRejectZeroCapacity() {
            DomainException exception = assertThrows(
                    DomainException.class,
                    () -> new Room("Room", 0L)
            );

            assertEquals("Capacity must be greater than zero", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("When using the full constructor (persistence reconstruction)")
    class FullConstructor {

        @Test
        @DisplayName("should accept all provided values without validation")
        void shouldAcceptAllValues() {
            var externalId = UUID.randomUUID();

            var room = new Room(1L, externalId, "Board Room", 20L, false);

            assertEquals(externalId, room.getExternalId());
            assertEquals("Board Room", room.getName());
            assertEquals(20L, room.getCapacity());
            assertFalse(room.isActive());
        }
    }
}
