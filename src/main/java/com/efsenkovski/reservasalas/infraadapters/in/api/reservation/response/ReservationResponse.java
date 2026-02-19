package com.efsenkovski.reservasalas.infraadapters.in.api.reservation.response;

import com.efsenkovski.reservasalas.common.ReservationStatus;
import com.efsenkovski.reservasalas.core.domain.model.Reservation;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReservationResponse(UUID id, ReservationUser user, ReservationRoom room, ReservationStatus status,
                                  LocalDateTime startDate, LocalDateTime endDate, LocalDateTime createdAt) {

    public ReservationResponse(Reservation reservation) {
        this(
                reservation.getExternalId(),
                new ReservationUser(reservation.getUser().getName(), reservation.getUser().getEmail()),
                new ReservationRoom(reservation.getRoom().getName(), reservation.getRoom().getCapacity()),
                reservation.getStatus(),
                reservation.getStartDate(),
                reservation.getEndDate(),
                reservation.getCreatedAt()
        );
    }
}
