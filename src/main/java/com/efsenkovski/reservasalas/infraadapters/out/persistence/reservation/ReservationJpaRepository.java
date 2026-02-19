package com.efsenkovski.reservasalas.infraadapters.out.persistence.reservation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReservationJpaRepository extends JpaRepository<ReservationEntity, Long> {
    Optional<ReservationEntity> findByExternalId(UUID externalId);
    @Query("SELECT r from ReservationEntity r WHERE r.room.externalId = :roomId AND r.status = 'ACTIVE' AND r.startDate < :endDate AND r.endDate > :startDate")
    List<ReservationEntity> findAllByRoomWithinPeriod(UUID roomId, LocalDateTime startDate, LocalDateTime endDate);
}
