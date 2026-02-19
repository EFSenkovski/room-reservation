package com.efsenkovski.reservasalas.core.application;

import com.efsenkovski.reservasalas.core.domain.exception.DomainException;
import com.efsenkovski.reservasalas.core.domain.exception.ResourceNotFoundException;
import com.efsenkovski.reservasalas.core.domain.model.Reservation;
import com.efsenkovski.reservasalas.core.domain.port.in.ReservationService;
import com.efsenkovski.reservasalas.core.domain.port.out.ReservationRepository;
import com.efsenkovski.reservasalas.core.domain.port.out.RoomRepository;
import com.efsenkovski.reservasalas.core.domain.port.out.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ReservationServiceImpl implements ReservationService {

    private final UserRepository userRepo;
    private final RoomRepository roomRepo;
    private final ReservationRepository reservationRepo;

    public ReservationServiceImpl(UserRepository userRepo, RoomRepository roomRepo, ReservationRepository reservationRepo) {
        this.userRepo = userRepo;
        this.roomRepo = roomRepo;
        this.reservationRepo = reservationRepo;
    }

    @Override
    public Reservation createReservation(UUID userId, UUID roomId, LocalDateTime startDate, LocalDateTime endDate) {
        var user = userRepo.findByExternalId(userId).orElseThrow(() -> new ResourceNotFoundException("User does not exist!"));
        var room = roomRepo.findByExternalId(roomId).orElseThrow(() -> new ResourceNotFoundException("Room does not exist!"));
        if (endDate.isBefore(startDate))
            throw new DomainException("Start date cannot be after end date"); // TODO this is feeling clumsy, I'm telling you!
        var overlappingReservations = reservationRepo.findAllByRoomWithinPeriod(room, startDate, endDate);
        if (!overlappingReservations.isEmpty())
            throw new DomainException("There is already an overlapping reservation for this room!");
        var reservation = new Reservation(user, room, startDate, endDate);
        return this.reservationRepo.saveReservation(reservation);
    }

    @Override
    public List<Reservation> getAllReservations() {
        return this.reservationRepo.findAll();
    }

    @Override
    public Reservation getReservationById(UUID externalId) {
        return this.reservationRepo.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + externalId));
    }
}
