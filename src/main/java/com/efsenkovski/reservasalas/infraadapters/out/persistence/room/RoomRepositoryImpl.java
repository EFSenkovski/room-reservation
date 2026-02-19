package com.efsenkovski.reservasalas.infraadapters.out.persistence.room;

import com.efsenkovski.reservasalas.core.domain.exception.DomainException;
import com.efsenkovski.reservasalas.core.domain.model.Room;
import com.efsenkovski.reservasalas.core.domain.port.out.RoomRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class RoomRepositoryImpl implements RoomRepository {

    private final RoomJpaRepository roomJpaRepository;
    private final RoomMapper mapper;

    public RoomRepositoryImpl(RoomJpaRepository roomJpaRepository, RoomMapper roomMapper) {
        this.roomJpaRepository = roomJpaRepository;
        this.mapper = roomMapper;
    }

    @Override
    public Room saveRoom(Room room) {
        if (roomJpaRepository.findByName(room.getName()).isPresent()) {
            throw new DomainException("Room with name " + room.getName() + " already exists");
        }
        return mapper.toRoom(this.roomJpaRepository.save(mapper.toEntity(room)));
    }

    @Override
    public List<Room> findAll() {
        return this.roomJpaRepository.findAll().stream()
                .map(mapper::toRoom)
                .toList();
    }

    @Override
    public Optional<Room> findByExternalId(UUID externalId) {
        return this.roomJpaRepository.findByExternalId(externalId)
                .map(mapper::toRoom);
    }

}
