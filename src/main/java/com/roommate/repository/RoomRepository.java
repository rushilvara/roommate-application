package com.roommate.repository;

import com.roommate.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByOwnerId(Long ownerId);

    List<Room> findByOwnerIdNot(Long ownerId);

    List<Room> findByLocationContainingIgnoreCaseAndMonthlyRentBetween(String location, BigDecimal minRent, BigDecimal maxRent);
}
