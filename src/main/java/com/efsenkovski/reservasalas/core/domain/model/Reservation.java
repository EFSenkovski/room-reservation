package com.efsenkovski.reservasalas.core.domain.model;

import com.efsenkovski.reservasalas.common.ReservationStatus;
import com.efsenkovski.reservasalas.core.domain.exception.DomainException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

public class Reservation {
    private UUID externalId;
    private User user;
    private Room room;
    private ReservationStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createdAt;

    public Reservation(UUID externalId, User user, Room room, ReservationStatus status, LocalDateTime startDate, LocalDateTime endDate, LocalDateTime createdAt) {
        this.externalId = externalId;
        this.user = user;
        this.room = room;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = createdAt;
    }

    public Reservation(User user, Room room, LocalDateTime startDate, LocalDateTime endDate) {
        if (!room.isActive()) throw new DomainException("Room cannot be inactive");
        if (endDate.isBefore(startDate)) throw new DomainException("Start date cannot be after end date");
        if (Duration.between(startDate, endDate).toMinutes() < 15)
            throw new DomainException("Reservation duration cannot be less than 15 minutes");

        this.externalId = UUID.randomUUID();
        this.user = user;
        this.room = room;
        this.endDate = endDate;
        this.startDate = startDate;
        this.createdAt = LocalDateTime.now();
        this.status = ReservationStatus.ACTIVE;
    }

    public UUID getExternalId() {
        return externalId;
    }

    public User getUser() {
        return user;
    }

    public Room getRoom() {
        return room;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
