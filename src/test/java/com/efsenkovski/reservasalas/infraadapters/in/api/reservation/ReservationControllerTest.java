package com.efsenkovski.reservasalas.infraadapters.in.api.reservation;

import com.efsenkovski.reservasalas.common.ReservationStatus;
import com.efsenkovski.reservasalas.core.domain.exception.ResourceNotFoundException;
import com.efsenkovski.reservasalas.core.domain.model.Reservation;
import com.efsenkovski.reservasalas.core.domain.model.Room;
import com.efsenkovski.reservasalas.core.domain.model.User;
import com.efsenkovski.reservasalas.core.domain.port.in.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationController.class)
@DisplayName("ReservationController")
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservationService reservationService;

    private User user;
    private Room room;
    private UUID userId;
    private UUID roomId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        roomId = UUID.randomUUID();
        user = new User(userId, "Alice", "alice@example.com", LocalDateTime.now());
        room = new Room(1L, roomId, "Meeting Room", 10L, true);
    }

    @Nested
    @DisplayName("POST /reservations")
    class CreateReservation {

        @Test
        @DisplayName("should return 201 Created with location header")
        void shouldReturn201WithLocation() throws Exception {
            var startDate = LocalDateTime.of(2026, 4, 1, 10, 0);
            var endDate = LocalDateTime.of(2026, 4, 1, 11, 0);
            var reservation = new Reservation(
                    UUID.randomUUID(), user, room, ReservationStatus.ACTIVE,
                    startDate, endDate, LocalDateTime.now()
            );
            when(reservationService.createReservation(any(), any(), any(), any()))
                    .thenReturn(reservation);

            mockMvc.perform(post("/reservations")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                        "userId": "%s",
                                        "roomId": "%s",
                                        "startDate": "01/04/2026T10:00",
                                        "endDate": "01/04/2026T11:00"
                                    }
                                    """.formatted(userId, roomId)))
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Location"))
                    .andExpect(jsonPath("$.id").value(reservation.getExternalId().toString()))
                    .andExpect(jsonPath("$.status").value("ACTIVE"));
        }

        @Test
        @DisplayName("should return 400 when required fields are null")
        void shouldReturn400WhenFieldsNull() throws Exception {
            mockMvc.perform(post("/reservations")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {}
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors").exists());
        }
    }

    @Nested
    @DisplayName("GET /reservations")
    class GetAllReservations {

        @Test
        @DisplayName("should return 200 with list of reservations")
        void shouldReturn200WithList() throws Exception {
            var startDate = LocalDateTime.of(2026, 4, 1, 10, 0);
            var endDate = LocalDateTime.of(2026, 4, 1, 11, 0);
            var r1 = new Reservation(
                    UUID.randomUUID(), user, room, ReservationStatus.ACTIVE,
                    startDate, endDate, LocalDateTime.now()
            );
            when(reservationService.getAllReservations()).thenReturn(List.of(r1));

            mockMvc.perform(get("/reservations"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].status").value("ACTIVE"));
        }
    }

    @Nested
    @DisplayName("GET /reservations/{id}")
    class GetReservationById {

        @Test
        @DisplayName("should return 200 when reservation found")
        void shouldReturn200WhenFound() throws Exception {
            var externalId = UUID.randomUUID();
            var startDate = LocalDateTime.of(2026, 4, 1, 10, 0);
            var endDate = LocalDateTime.of(2026, 4, 1, 11, 0);
            var reservation = new Reservation(
                    externalId, user, room, ReservationStatus.ACTIVE,
                    startDate, endDate, LocalDateTime.now()
            );
            when(reservationService.getReservationById(externalId)).thenReturn(reservation);

            mockMvc.perform(get("/reservations/{id}", externalId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(externalId.toString()))
                    .andExpect(jsonPath("$.user.name").value("Alice"))
                    .andExpect(jsonPath("$.room.name").value("Meeting Room"));
        }

        @Test
        @DisplayName("should return 404 when reservation not found")
        void shouldReturn404WhenNotFound() throws Exception {
            var externalId = UUID.randomUUID();
            when(reservationService.getReservationById(externalId))
                    .thenThrow(new ResourceNotFoundException("Reservation not found with id: " + externalId));

            mockMvc.perform(get("/reservations/{id}", externalId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }
    }
}
