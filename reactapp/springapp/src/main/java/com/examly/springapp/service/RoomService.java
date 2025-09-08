package com.examly.springapp.service;

import com.examly.springapp.model.Room;
import com.examly.springapp.repository.RoomRepository;
import com.examly.springapp.repository.BookingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoomService {
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    public RoomService(RoomRepository roomRepository, BookingRepository bookingRepository) {
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public List<Room> getAvailableRooms() {
        return roomRepository.findByAvailableTrue();
    }

    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + id));
    }

    public Room saveRoom(Room room) {
        return roomRepository.save(room);
    }

    @Transactional
    public void freeRoom(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + roomId));
        
        // Cancel any approved bookings for this room
        bookingRepository.findAll().stream()
                .filter(booking -> booking.getRoom() != null 
                        && booking.getRoom().getRoomId() != null
                        && booking.getRoom().getRoomId().equals(roomId) 
                        && "APPROVED".equals(booking.getStatus()))
                .forEach(booking -> booking.setStatus("CANCELLED"));
        
        // Set room as available
        room.setAvailable(true);
        roomRepository.save(room);
    }

    @Transactional
    public void freeAllRooms() {
        // Delete all approved bookings
        bookingRepository.findAll().stream()
                .filter(booking -> "APPROVED".equals(booking.getStatus()) || "PENDING".equals(booking.getStatus()))
                .forEach(booking -> {
                    booking.setStatus("CANCELLED");
                    bookingRepository.save(booking);
                });
        
        // Set all rooms as available
        List<Room> rooms = roomRepository.findAll();
        for (Room room : rooms) {
            room.setAvailable(true);
        }
        roomRepository.saveAll(rooms);
    }
}
