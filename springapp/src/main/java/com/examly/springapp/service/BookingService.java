package com.examly.springapp.service;

import com.examly.springapp.model.Booking;
import com.examly.springapp.model.Room;
import com.examly.springapp.repository.BookingRepository;
import com.examly.springapp.repository.RoomRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final NotificationService notificationService;

    public BookingService(BookingRepository bookingRepository, RoomRepository roomRepository, NotificationService notificationService) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public Booking createBooking(Booking booking) {
        Room room = roomRepository.findById(booking.getRoom().getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + booking.getRoom().getRoomId()));
        if (!room.getAvailable()) {
            throw new RuntimeException("Room is not available");
        }
        if (!booking.getGuestEmail().matches("^(.+)@(.+)$")) {
            throw new RuntimeException("Invalid email format");
        }
        if (!booking.getCheckOutDate().isAfter(booking.getCheckInDate())) {
            throw new RuntimeException("Check-out date must be after check-in date");
        }

        long nights = booking.getCheckOutDate().toEpochDay() - booking.getCheckInDate().toEpochDay();
        booking.setTotalPrice(nights * room.getPricePerNight());
        booking.setStatus("PENDING");
        booking.setCreatedAt(LocalDateTime.now());
        Booking savedBooking = bookingRepository.save(booking);
        notificationService.sendBookingConfirmation(savedBooking);
        return savedBooking;
    }

   public List<Booking> getAllBookings() {
return bookingRepository.findAll();
}

public Booking getBookingById(Long id) {
return bookingRepository.findById(id)
.orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));
}

@Transactional
public Booking updateBookingStatus(Long id, String status) {
    Booking booking = bookingRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));

    if (!"APPROVED".equalsIgnoreCase(status) && !"REJECTED".equalsIgnoreCase(status) && !"CANCELLED".equalsIgnoreCase(status)) {
        throw new RuntimeException("Invalid status: " + status);
    }

    booking.setStatus(status.toUpperCase());

    // Update room availability based on booking status
    Room room = booking.getRoom();
    if ("APPROVED".equalsIgnoreCase(status)) {
        room.setAvailable(false);
    } else if ("REJECTED".equalsIgnoreCase(status) || "CANCELLED".equalsIgnoreCase(status)) {
        room.setAvailable(true);
    }
    roomRepository.save(room);

    Booking updatedBooking = bookingRepository.save(booking);
    notificationService.sendBookingStatusUpdate(updatedBooking);
    return updatedBooking;
}

@Transactional
public Booking processPayment(Long bookingId) {
    // This reuses the same logic as updating status to APPROVED
    // and ensures the room is marked as unavailable.
    return updateBookingStatus(bookingId, "APPROVED");
}

@Transactional
public void cancelBooking(Long id) {
Booking booking = bookingRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));
    updateBookingStatus(id, "CANCELLED");
}

}