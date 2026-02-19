package com.efsenkovski.reservasalas.infraadapters.in.api.reservation;

import com.efsenkovski.reservasalas.core.domain.port.in.ReservationService;
import com.efsenkovski.reservasalas.infraadapters.in.api.reservation.request.CreateReservationRequest;
import com.efsenkovski.reservasalas.infraadapters.in.api.reservation.response.ReservationResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(@Valid @RequestBody CreateReservationRequest createReservationRequest) {
        var reservation = this.reservationService.createReservation(createReservationRequest.userId(),
                createReservationRequest.roomId(),
                createReservationRequest.startDate(),
                createReservationRequest.endDate()
        );

        var reservationReponse = new ReservationResponse(reservation);
        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(reservationReponse.id())
                .toUri();
        return ResponseEntity.created(location).body(reservationReponse);
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getAllReservations() {
        return ResponseEntity.ok(this.reservationService.getAllReservations().stream()
                .map(ReservationResponse::new)
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> getReservationById(@PathVariable UUID id) {
        var reservation = this.reservationService.getReservationById(id);
        return ResponseEntity.ok(new ReservationResponse(reservation));
    }
}
