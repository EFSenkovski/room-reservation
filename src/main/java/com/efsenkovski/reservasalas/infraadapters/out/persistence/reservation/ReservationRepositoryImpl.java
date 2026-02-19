package com.efsenkovski.reservasalas.infraadapters.out.persistence.reservation;

import com.efsenkovski.reservasalas.core.domain.model.Reservation;
import com.efsenkovski.reservasalas.core.domain.model.Room;
import com.efsenkovski.reservasalas.core.domain.port.out.ReservationRepository;
import com.efsenkovski.reservasalas.infraadapters.out.persistence.room.RoomEntity;
import com.efsenkovski.reservasalas.infraadapters.out.persistence.room.RoomJpaRepository;
import com.efsenkovski.reservasalas.infraadapters.out.persistence.user.UserEntity;
import com.efsenkovski.reservasalas.infraadapters.out.persistence.user.UserJpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ReservationRepositoryImpl implements ReservationRepository {

    private final ReservationJpaRepository reservationJpaRepository;
    private final RoomJpaRepository roomJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final ReservationMapper mapper;

    public ReservationRepositoryImpl(ReservationJpaRepository reservationJpaRepository,
                                     RoomJpaRepository roomJpaRepository,
                                     UserJpaRepository userJpaRepository,
                                     ReservationMapper reservationMapper) {
        this.reservationJpaRepository = reservationJpaRepository;
        this.roomJpaRepository = roomJpaRepository;
        this.userJpaRepository = userJpaRepository;
        this.mapper = reservationMapper;
    }

    @Override
    public Reservation saveReservation(Reservation reservation) {
        RoomEntity roomEntity = roomJpaRepository.findByExternalId(reservation.getRoom().getExternalId())
                .orElseThrow();
        UserEntity userEntity = userJpaRepository.findByExternalId(reservation.getUser().getExternalId())
                .orElseThrow();

        ReservationEntity entity = new ReservationEntity(
                reservation.getExternalId(),
                userEntity,
                roomEntity,
                reservation.getStatus().name(),
                reservation.getStartDate(),
                reservation.getEndDate(),
                reservation.getCreatedAt()
        );

        return this.mapper.toReservation(this.reservationJpaRepository.save(entity));
    }

    @Override
    public List<Reservation> findAll() {
        return this.reservationJpaRepository.findAll().stream()
                .map(mapper::toReservation)
                .toList();
    }

    @Override
    public Optional<Reservation> findByExternalId(UUID externalId) {
        return this.reservationJpaRepository.findByExternalId(externalId)
                .map(mapper::toReservation);
    }

    @Override
    public List<Reservation> findAllByRoomWithinPeriod(Room room, LocalDateTime startDate, LocalDateTime endDate) {
        return this.reservationJpaRepository.findAllByRoomWithinPeriod(room.getExternalId(), startDate, endDate).stream()
                .map(mapper::toReservation)
                .toList();
    }
}
