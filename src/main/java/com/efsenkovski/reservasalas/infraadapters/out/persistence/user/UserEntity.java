package com.efsenkovski.reservasalas.infraadapters.out.persistence.user;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class UserEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Column
    private UUID externalId;

    @Column
    private String name;

    @Column
    private String email;

    @Column
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
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

    public UserEntity() {
    }

    public UserEntity(UUID externalId, String name, String email, LocalDateTime createdAt) {
        this.externalId = externalId;
        this.name = name;
        this.email = email;
        this.createdAt = createdAt;
    }
}
