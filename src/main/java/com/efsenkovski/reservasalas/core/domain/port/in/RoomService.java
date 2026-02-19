package com.efsenkovski.reservasalas.core.domain.port.in;

import com.efsenkovski.reservasalas.core.domain.model.Room;

import java.util.List;
import java.util.UUID;

public interface RoomService {
    Room createRoom(String name, Long capacity);
    List<Room> getAllRooms();
    Room getRoomById(UUID externalId);
}
