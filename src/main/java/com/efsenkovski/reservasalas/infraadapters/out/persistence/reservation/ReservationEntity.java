package com.efsenkovski.reservasalas.infraadapters.out.persistence.reservation;

import com.efsenkovski.reservasalas.infraadapters.out.persistence.room.RoomEntity;
import com.efsenkovski.reservasalas.infraadapters.out.persistence.user.UserEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class ReservationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private UUID externalId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private RoomEntity room;

    @Column
    private String status;

    @Column
    private LocalDateTime startDate;

    @Column
    private LocalDateTime endDate;

    @Column
    private LocalDateTime createdAt;

    public ReservationEntity() {
    }

    public ReservationEntity(UUID externalId, UserEntity user, RoomEntity room, String status, LocalDateTime startDate, LocalDateTime endDate, LocalDateTime createdAt) {
        this.externalId = externalId;
        this.user = user;
        this.room = room;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = createdAt;

    }

    public Long getId() {
        return id;
    }

    public UUID getExternalId() {
        return externalId;
    }

    public UserEntity getUser() {
        return user;
    }

    public RoomEntity getRoom() {
        return room;
    }

    public String getStatus() {
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
