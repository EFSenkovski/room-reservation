package com.efsenkovski.reservasalas.infraadapters.out.persistence.room;

import com.efsenkovski.reservasalas.core.domain.exception.DomainException;
import com.efsenkovski.reservasalas.core.domain.model.Room;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RoomRepositoryImpl")
class RoomRepositoryImplTest {

    @Mock
    private RoomJpaRepository jpaRepository;

    @Mock
    private RoomMapper mapper;

    @InjectMocks
    private RoomRepositoryImpl repository;

    @Nested
    @DisplayName("saveRoom")
    class SaveRoom {

        @Test
        @DisplayName("should save and return mapped room when name is unique")
        void shouldSaveSuccessfully() {
            var room = new Room("Meeting Room", 10L);
            var entity = new RoomEntity(room.getExternalId(), room.getName(), room.getCapacity());

            when(jpaRepository.findByName(room.getName())).thenReturn(Optional.empty());
            when(mapper.toEntity(room)).thenReturn(entity);
            when(jpaRepository.save(entity)).thenReturn(entity);
            when(mapper.toRoom(entity)).thenReturn(room);

            var result = repository.saveRoom(room);

            assertEquals(room, result);
            verify(jpaRepository).save(entity);
        }

        @Test
        @DisplayName("should throw DomainException when room name already exists")
        void shouldThrowWhenDuplicateName() {
            var room = new Room("Meeting Room", 10L);
            var existingEntity = new RoomEntity(UUID.randomUUID(), "Meeting Room", 10L);

            when(jpaRepository.findByName(room.getName())).thenReturn(Optional.of(existingEntity));

            var exception = assertThrows(DomainException.class, () -> repository.saveRoom(room));

            assertTrue(exception.getMessage().contains("Meeting Room"));
            verify(jpaRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        @DisplayName("should return mapped list of rooms")
        void shouldReturnMappedList() {
            var entity1 = new RoomEntity(UUID.randomUUID(), "Room A", 10L);
            var entity2 = new RoomEntity(UUID.randomUUID(), "Room B", 20L);
            var room1 = new Room(1L, entity1.getExternalId(), "Room A", 10L, true);
            var room2 = new Room(2L, entity2.getExternalId(), "Room B", 20L, true);

            when(jpaRepository.findAll()).thenReturn(List.of(entity1, entity2));
            when(mapper.toRoom(entity1)).thenReturn(room1);
            when(mapper.toRoom(entity2)).thenReturn(room2);

            var result = repository.findAll();

            assertEquals(2, result.size());
            assertEquals("Room A", result.get(0).getName());
            assertEquals("Room B", result.get(1).getName());
        }
    }

    @Nested
    @DisplayName("findByExternalId")
    class FindByExternalId {

        @Test
        @DisplayName("should return mapped room when found")
        void shouldReturnWhenFound() {
            var externalId = UUID.randomUUID();
            var entity = new RoomEntity(externalId, "Room A", 10L);
            var room = new Room(1L, externalId, "Room A", 10L, true);

            when(jpaRepository.findByExternalId(externalId)).thenReturn(Optional.of(entity));
            when(mapper.toRoom(entity)).thenReturn(room);

            var result = repository.findByExternalId(externalId);

            assertTrue(result.isPresent());
            assertEquals("Room A", result.get().getName());
        }

        @Test
        @DisplayName("should return empty when not found")
        void shouldReturnEmptyWhenNotFound() {
            var externalId = UUID.randomUUID();
            when(jpaRepository.findByExternalId(externalId)).thenReturn(Optional.empty());

            var result = repository.findByExternalId(externalId);

            assertTrue(result.isEmpty());
        }
    }
}
