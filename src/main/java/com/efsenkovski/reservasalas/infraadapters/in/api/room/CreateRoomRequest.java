package com.efsenkovski.reservasalas.infraadapters.in.api.room;

import jakarta.validation.constraints.NotBlank;

public record CreateRoomRequest(@NotBlank(message = "Name is mandatory!") String name, Long capacity) {
}
