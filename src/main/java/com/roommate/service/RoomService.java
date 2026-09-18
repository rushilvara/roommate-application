package com.roommate.service;

import com.roommate.dto.RoomRequest;
import com.roommate.model.Room;
import com.roommate.model.User;
import com.roommate.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    public List<Room> findByOwnerId(Long ownerId) {
        return roomRepository.findByOwnerId(ownerId);
    }

    public Room getRoom(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found."));
    }

    @Transactional
    public Room createRoom(RoomRequest request, User owner) {
        Room room = new Room();
        applyRequest(room, request);
        room.setOwner(owner);
        return roomRepository.save(room);
    }

    @Transactional
    public Room updateRoom(Long roomId, RoomRequest request, Long actingUserId) {
        Room room = getRoom(roomId);
        verifyOwner(room, actingUserId);
        applyRequest(room, request);
        return roomRepository.save(room);
    }

    @Transactional
    public void deleteRoom(Long roomId, Long actingUserId) {
        Room room = getRoom(roomId);
        verifyOwner(room, actingUserId);
        roomRepository.delete(room);
    }

    public List<Room> search(String location, BigDecimal minRent, BigDecimal maxRent) {
        BigDecimal lower = minRent == null ? BigDecimal.ZERO : minRent;
        BigDecimal upper = maxRent == null ? new BigDecimal("1000000") : maxRent;
        String query = location == null ? "" : location;
        return roomRepository.findByLocationContainingIgnoreCaseAndMonthlyRentBetween(query, lower, upper);
    }

    private void verifyOwner(Room room, Long actingUserId) {
        if (!room.getOwner().getId().equals(actingUserId)) {
            throw new IllegalStateException("You are not allowed to modify this room.");
        }
    }

    private void applyRequest(Room room, RoomRequest request) {
        room.setTitle(request.getTitle());
        room.setDescription(request.getDescription());
        room.setLocation(request.getLocation());
        room.setMonthlyRent(request.getMonthlyRent());
        room.setAvailableFrom(request.getAvailableFrom());
        room.setFurnished(request.isFurnished());
    }
}
