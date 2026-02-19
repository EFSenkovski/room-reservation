package com.efsenkovski.reservasalas.infraadapters.in.api.room;

import com.efsenkovski.reservasalas.core.domain.exception.ResourceNotFoundException;
import com.efsenkovski.reservasalas.core.domain.model.Room;
import com.efsenkovski.reservasalas.core.domain.port.in.RoomService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoomController.class)
@DisplayName("RoomController")
class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoomService roomService;

    @Nested
    @DisplayName("POST /rooms")
    class CreateRoom {

        @Test
        @DisplayName("should return 201 Created with location header")
        void shouldReturn201WithLocation() throws Exception {
            var room = new Room(1L, UUID.randomUUID(), "Meeting Room", 10L, true);
            when(roomService.createRoom(eq("Meeting Room"), eq(10L))).thenReturn(room);

            mockMvc.perform(post("/rooms")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"name": "Meeting Room", "capacity": 10}
                                    """))
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Location"))
                    .andExpect(jsonPath("$.id").value(room.getExternalId().toString()))
                    .andExpect(jsonPath("$.name").value("Meeting Room"))
                    .andExpect(jsonPath("$.capacity").value(10))
                    .andExpect(jsonPath("$.active").value(true));
        }

        @Test
        @DisplayName("should return 400 when name is blank")
        void shouldReturn400WhenNameBlank() throws Exception {
            mockMvc.perform(post("/rooms")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"name": "", "capacity": 10}
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.name").value("Name is mandatory!"));
        }
    }

    @Nested
    @DisplayName("GET /rooms")
    class GetAllRooms {

        @Test
        @DisplayName("should return 200 with list of rooms")
        void shouldReturn200WithList() throws Exception {
            var room1 = new Room(1L, UUID.randomUUID(), "Room A", 10L, true);
            var room2 = new Room(2L, UUID.randomUUID(), "Room B", 20L, true);
            when(roomService.getAllRooms()).thenReturn(List.of(room1, room2));

            mockMvc.perform(get("/rooms"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].name").value("Room A"))
                    .andExpect(jsonPath("$[1].name").value("Room B"));
        }
    }

    @Nested
    @DisplayName("GET /rooms/{id}")
    class GetRoomById {

        @Test
        @DisplayName("should return 200 when room found")
        void shouldReturn200WhenFound() throws Exception {
            var externalId = UUID.randomUUID();
            var room = new Room(1L, externalId, "Room A", 10L, true);
            when(roomService.getRoomById(externalId)).thenReturn(room);

            mockMvc.perform(get("/rooms/{id}", externalId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(externalId.toString()))
                    .andExpect(jsonPath("$.name").value("Room A"));
        }

        @Test
        @DisplayName("should return 404 when room not found")
        void shouldReturn404WhenNotFound() throws Exception {
            var externalId = UUID.randomUUID();
            when(roomService.getRoomById(externalId))
                    .thenThrow(new ResourceNotFoundException("Room not found with id: " + externalId));

            mockMvc.perform(get("/rooms/{id}", externalId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }
    }
}
