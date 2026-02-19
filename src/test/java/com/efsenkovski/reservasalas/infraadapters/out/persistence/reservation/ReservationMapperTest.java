package com.efsenkovski.reservasalas.infraadapters.out.persistence.reservation;

import com.efsenkovski.reservasalas.common.ReservationStatus;
import com.efsenkovski.reservasalas.core.domain.model.Room;
import com.efsenkovski.reservasalas.core.domain.model.User;
import com.efsenkovski.reservasalas.infraadapters.out.persistence.room.RoomEntity;
import com.efsenkovski.reservasalas.infraadapters.out.persistence.room.RoomMapper;
import com.efsenkovski.reservasalas.infraadapters.out.persistence.user.UserEntity;
import com.efsenkovski.reservasalas.infraadapters.out.persistence.user.UserMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReservationMapper")
class ReservationMapperTest {

    @Mock
    private RoomMapper roomMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private ReservationMapper mapper;

    @Nested
    @DisplayName("toReservation")
    class ToReservation {

        @Test
        @DisplayName("should map all fields and delegate room/user mapping")
        void shouldMapAllFields() {
            var userEntity = new UserEntity(UUID.randomUUID(), "Alice", "alice@example.com", LocalDateTime.now());
            var roomEntity = new RoomEntity(UUID.randomUUID(), "Meeting Room", 10L);
            var externalId = UUID.randomUUID();
            var startDate = LocalDateTime.of(2026, 4, 1, 10, 0);
            var endDate = LocalDateTime.of(2026, 4, 1, 11, 0);
            var createdAt = LocalDateTime.of(2026, 3, 30, 8, 0);

            var entity = new ReservationEntity(externalId, userEntity, roomEntity,
                    "ACTIVE", startDate, endDate, createdAt);

            var domainUser = new User(userEntity.getExternalId(), "Alice", "alice@example.com", LocalDateTime.now());
            var domainRoom = new Room(1L, roomEntity.getExternalId(), "Meeting Room", 10L, true);

            when(userMapper.toUser(userEntity)).thenReturn(domainUser);
            when(roomMapper.toRoom(roomEntity)).thenReturn(domainRoom);

            var reservation = mapper.toReservation(entity);

            assertEquals(externalId, reservation.getExternalId());
            assertEquals(domainUser, reservation.getUser());
            assertEquals(domainRoom, reservation.getRoom());
            assertEquals(ReservationStatus.ACTIVE, reservation.getStatus());
            assertEquals(startDate, reservation.getStartDate());
            assertEquals(endDate, reservation.getEndDate());
            assertEquals(createdAt, reservation.getCreatedAt());
        }
    }
}
