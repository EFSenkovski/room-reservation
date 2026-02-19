package com.efsenkovski.reservasalas.core.application;

import com.efsenkovski.reservasalas.core.domain.exception.ResourceNotFoundException;
import com.efsenkovski.reservasalas.core.domain.model.Room;
import com.efsenkovski.reservasalas.core.domain.port.in.RoomService;
import com.efsenkovski.reservasalas.core.domain.port.out.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    public RoomServiceImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public Room createRoom(String name, Long capacity) {
        return this.roomRepository.saveRoom(new Room(name, capacity));
    }

    @Override
    public List<Room> getAllRooms() {
        return this.roomRepository.findAll();
    }

    @Override
    public Room getRoomById(UUID externalId) {
        return this.roomRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + externalId));
    }
}
