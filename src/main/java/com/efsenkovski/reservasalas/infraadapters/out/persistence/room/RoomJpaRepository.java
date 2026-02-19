package com.efsenkovski.reservasalas.infraadapters.out.persistence.room;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoomJpaRepository extends JpaRepository<RoomEntity, Long>{
    Optional<RoomEntity> findByName(String name);
    Optional<RoomEntity> findByExternalId(UUID externalId);
}
