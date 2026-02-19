package com.efsenkovski.reservasalas.infraadapters.out.persistence.reservation;

import com.efsenkovski.reservasalas.common.ReservationStatus;
import com.efsenkovski.reservasalas.core.domain.model.Reservation;
import com.efsenkovski.reservasalas.core.domain.model.Room;
import com.efsenkovski.reservasalas.core.domain.model.User;
import com.efsenkovski.reservasalas.infraadapters.out.persistence.room.RoomEntity;
import com.efsenkovski.reservasalas.infraadapters.out.persistence.room.RoomJpaRepository;
import com.efsenkovski.reservasalas.infraadapters.out.persistence.user.UserEntity;
import com.efsenkovski.reservasalas.infraadapters.out.persistence.user.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReservationRepositoryImpl")
class ReservationRepositoryImplTest {

    @Mock
    private ReservationJpaRepository reservationJpaRepository;

    @Mock
    private RoomJpaRepository roomJpaRepository;

    @Mock
    private UserJpaRepository userJpaRepository;

    @Mock
    private ReservationMapper mapper;

    @InjectMocks
    private ReservationRepositoryImpl repository;

    private User user;
    private Room room;
    private UserEntity userEntity;
    private RoomEntity roomEntity;
    private UUID roomExternalId;
    private UUID userExternalId;

    @BeforeEach
    void setUp() {
        userExternalId = UUID.randomUUID();
        roomExternalId = UUID.randomUUID();
        user = new User(userExternalId, "Alice", "alice@example.com", LocalDateTime.now());
        room = new Room(1L, roomExternalId, "Meeting Room", 10L, true);
        userEntity = new UserEntity(userExternalId, "Alice", "alice@example.com", LocalDateTime.now());
        roomEntity = new RoomEntity(roomExternalId, "Meeting Room", 10L);
    }

    @Nested
    @DisplayName("saveReservation")
    class SaveReservation {

        @Test
        @DisplayName("should look up entities, save, and return mapped reservation")
        void shouldSaveSuccessfully() {
            var startDate = LocalDateTime.of(2026, 4, 1, 10, 0);
            var endDate = LocalDateTime.of(2026, 4, 1, 11, 0);
            var reservation = new Reservation(
                    UUID.randomUUID(), user, room, ReservationStatus.ACTIVE,
                    startDate, endDate, LocalDateTime.now()
            );

            when(roomJpaRepository.findByExternalId(roomExternalId)).thenReturn(Optional.of(roomEntity));
            when(userJpaRepository.findByExternalId(userExternalId)).thenReturn(Optional.of(userEntity));
            when(reservationJpaRepository.save(any(ReservationEntity.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));
            when(mapper.toReservation(any(ReservationEntity.class))).thenReturn(reservation);

            var result = repository.saveReservation(reservation);

            assertNotNull(result);
            assertEquals(reservation.getExternalId(), result.getExternalId());
            verify(reservationJpaRepository).save(any(ReservationEntity.class));
        }
    }

    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        @DisplayName("should return mapped list of reservations")
        void shouldReturnMappedList() {
            var entity = new ReservationEntity(UUID.randomUUID(), userEntity, roomEntity,
                    "ACTIVE", LocalDateTime.now(), LocalDateTime.now().plusHours(1), LocalDateTime.now());
            var reservation = new Reservation(
                    entity.getExternalId(), user, room, ReservationStatus.ACTIVE,
                    entity.getStartDate(), entity.getEndDate(), entity.getCreatedAt()
            );

            when(reservationJpaRepository.findAll()).thenReturn(List.of(entity));
            when(mapper.toReservation(entity)).thenReturn(reservation);

            var result = repository.findAll();

            assertEquals(1, result.size());
            assertEquals(reservation.getExternalId(), result.get(0).getExternalId());
        }
    }

    @Nested
    @DisplayName("findByExternalId")
    class FindByExternalId {

        @Test
        @DisplayName("should return mapped reservation when found")
        void shouldReturnWhenFound() {
            var externalId = UUID.randomUUID();
            var entity = new ReservationEntity(externalId, userEntity, roomEntity,
                    "ACTIVE", LocalDateTime.now(), LocalDateTime.now().plusHours(1), LocalDateTime.now());
            var reservation = new Reservation(
                    externalId, user, room, ReservationStatus.ACTIVE,
                    entity.getStartDate(), entity.getEndDate(), entity.getCreatedAt()
            );

            when(reservationJpaRepository.findByExternalId(externalId)).thenReturn(Optional.of(entity));
            when(mapper.toReservation(entity)).thenReturn(reservation);

            var result = repository.findByExternalId(externalId);

            assertTrue(result.isPresent());
            assertEquals(externalId, result.get().getExternalId());
        }

        @Test
        @DisplayName("should return empty when not found")
        void shouldReturnEmptyWhenNotFound() {
            var externalId = UUID.randomUUID();
            when(reservationJpaRepository.findByExternalId(externalId)).thenReturn(Optional.empty());

            var result = repository.findByExternalId(externalId);

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("findAllByRoomWithinPeriod")
    class FindAllByRoomWithinPeriod {

        @Test
        @DisplayName("should delegate to JPA repository with room externalId and map results")
        void shouldDelegateAndMap() {
            var startDate = LocalDateTime.of(2026, 4, 1, 10, 0);
            var endDate = LocalDateTime.of(2026, 4, 1, 11, 0);
            var entity = new ReservationEntity(UUID.randomUUID(), userEntity, roomEntity,
                    "ACTIVE", startDate, endDate, LocalDateTime.now());
            var reservation = new Reservation(
                    entity.getExternalId(), user, room, ReservationStatus.ACTIVE,
                    startDate, endDate, entity.getCreatedAt()
            );

            when(reservationJpaRepository.findAllByRoomWithinPeriod(roomExternalId, startDate, endDate))
                    .thenReturn(List.of(entity));
            when(mapper.toReservation(entity)).thenReturn(reservation);

            var result = repository.findAllByRoomWithinPeriod(room, startDate, endDate);

            assertEquals(1, result.size());
            assertEquals(reservation.getExternalId(), result.get(0).getExternalId());
            verify(reservationJpaRepository).findAllByRoomWithinPeriod(roomExternalId, startDate, endDate);
        }
    }
}
