package com.efsenkovski.reservasalas.core.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class User {
    private UUID externalId;
    private String name;
    private String email;
    private LocalDateTime createdAt;

    public User(UUID externalId, String name, String email, LocalDateTime createdAt) {
        this.externalId = externalId;
        this.name = name;
        this.email = email;
        this.createdAt = createdAt;
    }

    public UUID getExternalId() {
        return externalId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public User(String name, String email) {
        this.externalId = UUID.randomUUID();
        this.name = name;
        this.email = email;
        this.createdAt = LocalDateTime.now();
    }
}
