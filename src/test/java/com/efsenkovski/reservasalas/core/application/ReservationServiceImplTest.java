package com.efsenkovski.reservasalas.core.application;

import com.efsenkovski.reservasalas.common.ReservationStatus;
import com.efsenkovski.reservasalas.core.domain.exception.DomainException;
import com.efsenkovski.reservasalas.core.domain.exception.ResourceNotFoundException;
import com.efsenkovski.reservasalas.core.domain.model.Reservation;
import com.efsenkovski.reservasalas.core.domain.model.Room;
import com.efsenkovski.reservasalas.core.domain.model.User;
import com.efsenkovski.reservasalas.core.domain.port.out.ReservationRepository;
import com.efsenkovski.reservasalas.core.domain.port.out.RoomRepository;
import com.efsenkovski.reservasalas.core.domain.port.out.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReservationServiceImpl")
class ReservationServiceImplTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private RoomRepository roomRepo;

    @Mock
    private ReservationRepository reservationRepo;

    @InjectMocks
    private ReservationServiceImpl service;

    private UUID userId;
    private UUID roomId;
    private User user;
    private Room room;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        roomId = UUID.randomUUID();
        user = new User(userId, "Alice", "alice@example.com", LocalDateTime.now());
        room = new Room(1L, roomId, "Meeting Room", 10L, true);
        startDate = LocalDateTime.of(2026, 4, 1, 10, 0);
        endDate = LocalDateTime.of(2026, 4, 1, 11, 0);
    }

    @Nested
    @DisplayName("createReservation")
    class CreateReservation {

        @Test
        @DisplayName("should create reservation successfully when all data is valid")
        void shouldCreateReservationSuccessfully() {
            when(userRepo.findByExternalId(userId)).thenReturn(Optional.of(user));
            when(roomRepo.findByExternalId(roomId)).thenReturn(Optional.of(room));
            when(reservationRepo.findAllByRoomWithinPeriod(any(), any(), any()))
                    .thenReturn(Collections.emptyList());
            when(reservationRepo.saveReservation(any(Reservation.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            Reservation result = service.createReservation(userId, roomId, startDate, endDate);

            assertNotNull(result);
            assertEquals(user, result.getUser());
            assertEquals(room, result.getRoom());
            assertEquals(startDate, result.getStartDate());
            assertEquals(endDate, result.getEndDate());
            assertEquals(ReservationStatus.ACTIVE, result.getStatus());

            verify(reservationRepo).saveReservation(any(Reservation.class));
            verify(userRepo, times(1)).findByExternalId(userId);
            verify(roomRepo, times(1)).findByExternalId(roomId);
        }

        @Test
        @DisplayName("should use ArgumentCaptor to inspect what was saved")
        void shouldSaveReservationWithCorrectData() {
            when(userRepo.findByExternalId(userId)).thenReturn(Optional.of(user));
            when(roomRepo.findByExternalId(roomId)).thenReturn(Optional.of(room));
            when(reservationRepo.findAllByRoomWithinPeriod(any(), any(), any()))
                    .thenReturn(Collections.emptyList());
            when(reservationRepo.saveReservation(any(Reservation.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            service.createReservation(userId, roomId, startDate, endDate);

            ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);
            verify(reservationRepo).saveReservation(captor.capture());

            Reservation savedReservation = captor.getValue();
            assertEquals(user, savedReservation.getUser());
            assertEquals(room, savedReservation.getRoom());
            assertEquals(startDate, savedReservation.getStartDate());
            assertEquals(endDate, savedReservation.getEndDate());
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when user does not exist")
        void shouldThrowWhenUserNotFound() {
            when(userRepo.findByExternalId(userId)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> service.createReservation(userId, roomId, startDate, endDate)
            );

            assertEquals("User does not exist!", exception.getMessage());
            verify(roomRepo, never()).findByExternalId(any());
            verify(reservationRepo, never()).saveReservation(any());
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when room does not exist")
        void shouldThrowWhenRoomNotFound() {
            when(userRepo.findByExternalId(userId)).thenReturn(Optional.of(user));
            when(roomRepo.findByExternalId(roomId)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> service.createReservation(userId, roomId, startDate, endDate)
            );

            assertEquals("Room does not exist!", exception.getMessage());
            verify(roomRepo).findByExternalId(roomId);
            verify(reservationRepo, never()).saveReservation(any());
        }

        @Test
        @DisplayName("should throw DomainException when end date is before start date")
        void shouldThrowWhenEndDateBeforeStartDate() {
            when(userRepo.findByExternalId(userId)).thenReturn(Optional.of(user));
            when(roomRepo.findByExternalId(roomId)).thenReturn(Optional.of(room));

            LocalDateTime badStart = LocalDateTime.of(2026, 4, 1, 14, 0);
            LocalDateTime badEnd = LocalDateTime.of(2026, 4, 1, 10, 0);

            DomainException exception = assertThrows(
                    DomainException.class,
                    () -> service.createReservation(userId, roomId, badStart, badEnd)
            );

            assertEquals("Start date cannot be after end date", exception.getMessage());
            verify(reservationRepo, never()).findAllByRoomWithinPeriod(any(), any(), any());
            verify(reservationRepo, never()).saveReservation(any());
        }

        @Test
        @DisplayName("should throw DomainException when there are overlapping reservations")
        void shouldThrowWhenOverlappingReservationsExist() {
            when(userRepo.findByExternalId(userId)).thenReturn(Optional.of(user));
            when(roomRepo.findByExternalId(roomId)).thenReturn(Optional.of(room));

            Reservation existingReservation = new Reservation(
                    UUID.randomUUID(), user, room, ReservationStatus.ACTIVE,
                    startDate, endDate, LocalDateTime.now()
            );
            when(reservationRepo.findAllByRoomWithinPeriod(any(), any(), any()))
                    .thenReturn(List.of(existingReservation));

            DomainException exception = assertThrows(
                    DomainException.class,
                    () -> service.createReservation(userId, roomId, startDate, endDate)
            );

            assertEquals("There is already an overlapping reservation for this room!",
                    exception.getMessage());
            verify(reservationRepo).findAllByRoomWithinPeriod(any(), any(), any());
            verify(reservationRepo, never()).saveReservation(any());
        }
    }

    @Nested
    @DisplayName("getAllReservations")
    class GetAllReservations {

        @Test
        @DisplayName("should return all reservations from the repository")
        void shouldReturnAllReservations() {
            Reservation r1 = new Reservation(
                    UUID.randomUUID(), user, room, ReservationStatus.ACTIVE,
                    startDate, endDate, LocalDateTime.now()
            );
            Reservation r2 = new Reservation(
                    UUID.randomUUID(), user, room, ReservationStatus.COMPLETED,
                    startDate.plusHours(2), endDate.plusHours(2), LocalDateTime.now()
            );

            when(reservationRepo.findAll()).thenReturn(List.of(r1, r2));

            List<Reservation> result = service.getAllReservations();

            assertEquals(2, result.size());
            verify(reservationRepo).findAll();
        }

        @Test
        @DisplayName("should return empty list when there are no reservations")
        void shouldReturnEmptyListWhenNoReservations() {
            when(reservationRepo.findAll()).thenReturn(Collections.emptyList());

            List<Reservation> result = service.getAllReservations();

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("getReservationById")
    class GetReservationById {

        @Test
        @DisplayName("should return reservation when it exists")
        void shouldReturnReservationWhenFound() {
            UUID reservationId = UUID.randomUUID();
            Reservation reservation = new Reservation(
                    reservationId, user, room, ReservationStatus.ACTIVE,
                    startDate, endDate, LocalDateTime.now()
            );

            when(reservationRepo.findByExternalId(reservationId))
                    .thenReturn(Optional.of(reservation));

            Reservation result = service.getReservationById(reservationId);

            assertNotNull(result);
            assertEquals(reservationId, result.getExternalId());
            assertEquals(user, result.getUser());
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when reservation does not exist")
        void shouldThrowWhenReservationNotFound() {
            UUID unknownId = UUID.randomUUID();

            when(reservationRepo.findByExternalId(unknownId))
                    .thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> service.getReservationById(unknownId)
            );

            assertTrue(exception.getMessage().contains(unknownId.toString()));
        }
    }
}
