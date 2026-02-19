package com.efsenkovski.reservasalas.infraadapters.out.persistence.room;

import com.efsenkovski.reservasalas.core.domain.model.Room;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RoomMapper")
class RoomMapperTest {

    private final RoomMapper mapper = new RoomMapper();

    @Nested
    @DisplayName("toRoom")
    class ToRoom {

        @Test
        @DisplayName("should map all fields from entity to domain model")
        void shouldMapAllFields() {
            var entity = new RoomEntity(UUID.randomUUID(), "Board Room", 15L);

            var room = mapper.toRoom(entity);

            assertEquals(entity.getId(), room.getExternalId() != null ? entity.getId() : null);
            assertEquals(entity.getExternalId(), room.getExternalId());
            assertEquals(entity.getName(), room.getName());
            assertEquals(entity.getCapacity(), room.getCapacity());
            assertEquals(entity.getActive(), room.isActive());
        }
    }

    @Nested
    @DisplayName("toEntity")
    class ToEntity {

        @Test
        @DisplayName("should map domain model fields to entity")
        void shouldMapToEntity() {
            var room = new Room(1L, UUID.randomUUID(), "Conference Room", 20L, true);

            var entity = mapper.toEntity(room);

            assertEquals(room.getExternalId(), entity.getExternalId());
            assertEquals(room.getName(), entity.getName());
            assertEquals(room.getCapacity(), entity.getCapacity());
        }
    }
}
