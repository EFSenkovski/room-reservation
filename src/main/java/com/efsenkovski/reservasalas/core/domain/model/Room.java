package com.efsenkovski.reservasalas.core.domain.model;

import com.efsenkovski.reservasalas.core.domain.exception.DomainException;

import java.util.UUID;

public class Room {
    private Long id;
    private UUID externalId;
    private String name;
    private Long capacity;
    private Boolean active;

    public Room(Long id, UUID externalId, String name, Long capacity, Boolean active) {
        this.id = id;
        this.externalId = externalId;
        this.name = name;
        this.capacity = capacity;
        this.active = active;
    }

    public Room(String name, Long capacity) {
        if (capacity <= 0) throw new DomainException("Capacity must be greater than zero");
        externalId = UUID.randomUUID();
        this.name = name;
        this.capacity = capacity;
        this.active = true;
    }

    public Boolean isActive() {
        return active;
    }

    public String getName() {
        return name;
    }

    public Long getCapacity() {
        return capacity;
    }

    public UUID getExternalId() {
        return externalId;
    }
}
