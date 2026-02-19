package com.efsenkovski.reservasalas.infraadapters.in.api.room;

import com.efsenkovski.reservasalas.core.domain.port.in.RoomService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping
    public ResponseEntity<RoomResponse> createRoom(@Valid @RequestBody CreateRoomRequest createRoomDto) {
        var room = this.roomService.createRoom(createRoomDto.name(), createRoomDto.capacity());
        var roomResponse = new RoomResponse(room);
        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(roomResponse.id())
                .toUri();
        return ResponseEntity.created(location).body(roomResponse);
    }

    @GetMapping
    public ResponseEntity<List<RoomResponse>> getAllRooms() {
        return ResponseEntity.ok(this.roomService.getAllRooms().stream()
                .map(RoomResponse::new)
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomResponse> getRoomById(@PathVariable UUID id) {
        var room = this.roomService.getRoomById(id);
        return ResponseEntity.ok(new RoomResponse(room));
    }

}
