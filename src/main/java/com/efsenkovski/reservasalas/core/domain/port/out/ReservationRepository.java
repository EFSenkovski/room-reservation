package com.efsenkovski.reservasalas.core.domain.port.out;

import com.efsenkovski.reservasalas.core.domain.model.Reservation;
import com.efsenkovski.reservasalas.core.domain.model.Room;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReservationRepository {
    Reservation saveReservation(Reservation reservation);
    List<Reservation> findAll();
    Optional<Reservation> findByExternalId(UUID externalId);
    List<Reservation> findAllByRoomWithinPeriod(Room room, LocalDateTime startDate, LocalDateTime endDate);
}
