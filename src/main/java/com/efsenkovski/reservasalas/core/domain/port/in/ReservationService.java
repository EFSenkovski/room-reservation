package com.efsenkovski.reservasalas.core.domain.port.in;

import com.efsenkovski.reservasalas.core.domain.model.Reservation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ReservationService {
    Reservation createReservation(UUID userId, UUID roomId, LocalDateTime startDate, LocalDateTime endDate);
    List<Reservation> getAllReservations();
    Reservation getReservationById(UUID externalId);
}
