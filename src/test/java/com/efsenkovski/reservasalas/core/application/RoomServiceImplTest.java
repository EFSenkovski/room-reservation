package com.efsenkovski.reservasalas.core.application;

import com.efsenkovski.reservasalas.core.domain.exception.ResourceNotFoundException;
import com.efsenkovski.reservasalas.core.domain.model.Room;
import com.efsenkovski.reservasalas.core.domain.port.out.RoomRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("RoomServiceImpl")
class RoomServiceImplTest {

    @Mock
    private RoomRepository roomRepo;

    @InjectMocks
    private RoomServiceImpl service;

    @Nested
    @DisplayName("createRoom")
    class CreateRoom {

        @Test
        @DisplayName("should create a room successfully when data is valid")
        void shouldCreateRoomSuccessfully() {
            when(roomRepo.saveRoom(any(Room.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            var result = service.createRoom("Meeting Room", 10L);

            assertNotNull(result);
            assertEquals("Meeting Room", result.getName());
            assertEquals(10L, result.getCapacity());
            verify(roomRepo).saveRoom(any(Room.class));
        }
    }

    @Nested
    @DisplayName("getAllRooms")
    class GetAllRooms {

        @Test
        @DisplayName("should return list of rooms")
        void shouldReturnListOfRooms() {
            var room1 = new Room("Room A", 10L);
            var room2 = new Room("Room B", 20L);
            when(roomRepo.findAll()).thenReturn(List.of(room1, room2));

            var result = service.getAllRooms();

            assertEquals(2, result.size());
            verify(roomRepo).findAll();
        }

        @Test
        @DisplayName("should return empty list when no rooms exist")
        void shouldReturnEmptyList() {
            when(roomRepo.findAll()).thenReturn(List.of());

            var result = service.getAllRooms();

            assertTrue(result.isEmpty());
            verify(roomRepo).findAll();
        }
    }

    @Nested
    @DisplayName("getRoomById")
    class GetRoomById {

        @Test
        @DisplayName("should return room when found")
        void shouldReturnRoomWhenFound() {
            var room = new Room("Room A", 10L);
            var externalId = room.getExternalId();
            when(roomRepo.findByExternalId(externalId)).thenReturn(Optional.of(room));

            var result = service.getRoomById(externalId);

            assertNotNull(result);
            assertEquals("Room A", result.getName());
            verify(roomRepo).findByExternalId(externalId);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when room not found")
        void shouldThrowWhenRoomNotFound() {
            var externalId = UUID.randomUUID();
            when(roomRepo.findByExternalId(externalId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> service.getRoomById(externalId));

            verify(roomRepo).findByExternalId(externalId);
        }
    }
}
