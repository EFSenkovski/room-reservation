package com.efsenkovski.reservasalas.infraadapters.out.persistence.room;

import com.efsenkovski.reservasalas.core.domain.model.Room;
import org.springframework.stereotype.Component;

@Component
public class RoomMapper {

    public Room toRoom(RoomEntity entity) {
        return new Room(
                entity.getId(),
                entity.getExternalId(),
                entity.getName(),
                entity.getCapacity(),
                entity.getActive()
        );
    }

    public RoomEntity toEntity(Room room) {
        return new RoomEntity(
                room.getExternalId(),
                room.getName(),
                room.getCapacity()
        );
    }

}
