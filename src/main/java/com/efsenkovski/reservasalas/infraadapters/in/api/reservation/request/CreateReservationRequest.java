package com.efsenkovski.reservasalas.infraadapters.in.api.reservation.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateReservationRequest(@NotNull UUID userId,
                                       @NotNull UUID roomId,
                                       @NotNull @JsonFormat(pattern = "dd/MM/yyyy'T'HH:mm") LocalDateTime startDate,
                                       @NotNull @JsonFormat(pattern = "dd/MM/yyyy'T'HH:mm") LocalDateTime endDate) {
}
