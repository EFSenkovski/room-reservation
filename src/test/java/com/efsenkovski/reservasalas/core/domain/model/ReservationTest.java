package com.efsenkovski.reservasalas.core.domain.model;

import com.efsenkovski.reservasalas.common.ReservationStatus;
import com.efsenkovski.reservasalas.core.domain.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Reservation domain model")
class ReservationTest {

    private Room createActiveRoom() {
        return new Room(1L, UUID.randomUUID(), "Conference Room A", 10L, true);
    }

    private Room createInactiveRoom() {
        return new Room(2L, UUID.randomUUID(), "Closed Room", 5L, false);
    }

    private User createUser() {
        return new User(UUID.randomUUID(), "John Doe", "john@example.com", LocalDateTime.now());
    }

    @Nested
    @DisplayName("When creating a valid reservation")
    class ValidReservation {

        @Test
        @DisplayName("should set all fields correctly")
        void shouldSetAllFieldsCorrectly() {
            User user = createUser();
            Room room = createActiveRoom();
            LocalDateTime start = LocalDateTime.of(2026, 3, 15, 10, 0);
            LocalDateTime end = LocalDateTime.of(2026, 3, 15, 11, 0);

            Reservation reservation = new Reservation(user, room, start, end);

            assertNotNull(reservation.getExternalId());
            assertEquals(user, reservation.getUser());
            assertEquals(room, reservation.getRoom());
            assertEquals(start, reservation.getStartDate());
            assertEquals(end, reservation.getEndDate());
            assertEquals(ReservationStatus.ACTIVE, reservation.getStatus());
            assertNotNull(reservation.getCreatedAt());
        }

        @Test
        @DisplayName("should accept a reservation of exactly 15 minutes (the minimum)")
        void shouldAcceptFifteenMinuteReservation() {
            User user = createUser();
            Room room = createActiveRoom();
            LocalDateTime start = LocalDateTime.of(2026, 3, 15, 10, 0);
            LocalDateTime end = LocalDateTime.of(2026, 3, 15, 10, 15);

            assertDoesNotThrow(() -> new Reservation(user, room, start, end));
        }

        @Test
        @DisplayName("should accept a multi-hour reservation")
        void shouldAcceptLongReservation() {
            User user = createUser();
            Room room = createActiveRoom();
            LocalDateTime start = LocalDateTime.of(2026, 3, 15, 8, 0);
            LocalDateTime end = LocalDateTime.of(2026, 3, 15, 17, 0);

            Reservation reservation = new Reservation(user, room, start, end);

            assertNotNull(reservation.getExternalId());
        }
    }

    @Nested
    @DisplayName("When creating a reservation with invalid data")
    class InvalidReservation {

        @Test
        @DisplayName("should reject reservation on an inactive room")
        void shouldRejectInactiveRoom() {
            User user = createUser();
            Room inactiveRoom = createInactiveRoom();
            LocalDateTime start = LocalDateTime.of(2026, 3, 15, 10, 0);
            LocalDateTime end = LocalDateTime.of(2026, 3, 15, 11, 0);

            DomainException exception = assertThrows(
                    DomainException.class,
                    () -> new Reservation(user, inactiveRoom, start, end)
            );

            assertEquals("Room cannot be inactive", exception.getMessage());
        }

        @Test
        @DisplayName("should reject reservation where end date is before start date")
        void shouldRejectEndBeforeStart() {
            User user = createUser();
            Room room = createActiveRoom();
            LocalDateTime start = LocalDateTime.of(2026, 3, 15, 14, 0);
            LocalDateTime end = LocalDateTime.of(2026, 3, 15, 10, 0);

            DomainException exception = assertThrows(
                    DomainException.class,
                    () -> new Reservation(user, room, start, end)
            );

            assertEquals("Start date cannot be after end date", exception.getMessage());
        }

        @Test
        @DisplayName("should reject reservation shorter than 15 minutes")
        void shouldRejectTooShortReservation() {
            User user = createUser();
            Room room = createActiveRoom();
            LocalDateTime start = LocalDateTime.of(2026, 3, 15, 10, 0);
            LocalDateTime end = LocalDateTime.of(2026, 3, 15, 10, 14);

            DomainException exception = assertThrows(
                    DomainException.class,
                    () -> new Reservation(user, room, start, end)
            );

            assertEquals("Reservation duration cannot be less than 15 minutes", exception.getMessage());
        }

        @Test
        @DisplayName("should reject reservation with zero duration (start == end)")
        void shouldRejectZeroDuration() {
            User user = createUser();
            Room room = createActiveRoom();
            LocalDateTime sameTime = LocalDateTime.of(2026, 3, 15, 10, 0);

            assertThrows(
                    DomainException.class,
                    () -> new Reservation(user, room, sameTime, sameTime)
            );
        }
    }

    @Nested
    @DisplayName("When using the full constructor (persistence reconstruction)")
    class FullConstructor {

        @Test
        @DisplayName("should accept all provided values without validation")
        void shouldAcceptAllValues() {
            UUID externalId = UUID.randomUUID();
            User user = createUser();
            Room room = createActiveRoom();
            LocalDateTime start = LocalDateTime.of(2026, 3, 15, 10, 0);
            LocalDateTime end = LocalDateTime.of(2026, 3, 15, 11, 0);
            LocalDateTime createdAt = LocalDateTime.of(2026, 3, 1, 8, 0);

            Reservation reservation = new Reservation(
                    externalId, user, room, ReservationStatus.CANCELLED, start, end, createdAt
            );

            assertEquals(externalId, reservation.getExternalId());
            assertEquals(user, reservation.getUser());
            assertEquals(room, reservation.getRoom());
            assertEquals(ReservationStatus.CANCELLED, reservation.getStatus());
            assertEquals(start, reservation.getStartDate());
            assertEquals(end, reservation.getEndDate());
            assertEquals(createdAt, reservation.getCreatedAt());
        }
    }
}
