package com.efsenkovski.reservasalas.core.domain.port.out;

import com.efsenkovski.reservasalas.core.domain.model.Room;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoomRepository {
    Room saveRoom(Room room);
    List<Room> findAll();
    Optional<Room> findByExternalId(UUID externalId);
}
