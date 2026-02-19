package com.efsenkovski.reservasalas.infraadapters.out.persistence.room;

import com.efsenkovski.reservasalas.infraadapters.out.persistence.reservation.ReservationEntity;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class RoomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private UUID externalId;

    @Column
    private String name;

    @Column
    private Long capacity;

    @Column
    private Boolean active;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<ReservationEntity> reservations;

    public RoomEntity() {
    }

    public RoomEntity(UUID externalId, String name, Long capacity) {
        this.externalId = externalId;
        this.name = name;
        this.capacity = capacity;
        this.active = true;
        this.reservations = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public UUID getExternalId() {
        return externalId;
    }

    public String getName() {
        return name;
    }

    public Long getCapacity() {
        return capacity;
    }

    public Boolean getActive() {
        return active;
    }

    public List<ReservationEntity> getReservations() {
        return reservations;
    }
}
