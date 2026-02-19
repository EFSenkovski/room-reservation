package com.efsenkovski.reservasalas.infraadapters.out.persistence.reservation;

import com.efsenkovski.reservasalas.common.ReservationStatus;
import com.efsenkovski.reservasalas.core.domain.model.Reservation;
import com.efsenkovski.reservasalas.infraadapters.out.persistence.room.RoomMapper;
import com.efsenkovski.reservasalas.infraadapters.out.persistence.user.UserMapper;
import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {

    private final RoomMapper roomMapper;
    private final UserMapper userMapper;

    public ReservationMapper(RoomMapper roomMapper, UserMapper userMapper) {
        this.roomMapper = roomMapper;
        this.userMapper = userMapper;
    }

    public Reservation toReservation(ReservationEntity entity) {
        return new Reservation(
                entity.getExternalId(),
                userMapper.toUser(entity.getUser()),
                roomMapper.toRoom(entity.getRoom()),
                ReservationStatus.valueOf(entity.getStatus()),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getCreatedAt()
        );
    }
}
