package com.efsenkovski.reservasalas.infraadapters.in.api.room;

import com.efsenkovski.reservasalas.core.domain.model.Room;

import java.util.UUID;

public record RoomResponse(UUID id, String name, Long capacity, Boolean active) {
    public RoomResponse(Room room) {
        this(room.getExternalId(),
                room.getName(),
                room.getCapacity(),
                room.isActive()
        );
    }
}
