package com.efsenkovski.reservasalas.infraadapters.in.api.user;

import com.efsenkovski.reservasalas.core.domain.model.User;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(UUID id, String name, String email, LocalDateTime createdAt) {
    public UserResponse(User user) {
        this(user.getExternalId(), user.getName(), user.getEmail(), user.getCreatedAt());
    }
}
