package com.examly.springapp.controller;

import com.examly.springapp.model.Booking;
import com.examly.springapp.model.Room;
import com.examly.springapp.service.BookingService;
import com.examly.springapp.repository.RoomRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    private final BookingService bookingService;
    private final RoomRepository roomRepository;

    public BookingController(BookingService bookingService, RoomRepository roomRepository) {
        this.bookingService = bookingService;
        this.roomRepository = roomRepository;
    }

@PostMapping
public ResponseEntity<?> createBooking(@RequestBody Map<String, Object> payload) {
    try {
        Long roomId = Long.valueOf(payload.get("roomId").toString());
        String guestName = payload.get("guestName").toString();
        String guestEmail = payload.get("guestEmail").toString();
        LocalDate checkIn = LocalDate.parse(payload.get("checkInDate").toString());
        LocalDate checkOut = LocalDate.parse(payload.get("checkOutDate").toString());

        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new RuntimeException("Room not found with id: " + roomId));

        // Calculate total price
        long nights = checkOut.toEpochDay() - checkIn.toEpochDay();
        double totalPrice = nights * room.getPricePerNight();

        Booking booking = new Booking();
        booking.setRoom(room);
        booking.setGuestName(guestName);
        booking.setGuestEmail(guestEmail);
        booking.setCheckInDate(checkIn);
        booking.setCheckOutDate(checkOut);
        booking.setTotalPrice(totalPrice);
        booking.setStatus("PENDING");
        booking.setCreatedAt(LocalDateTime.now());

        Booking saved = bookingService.createBooking(booking);
        return ResponseEntity.status(201).body(saved);

    } catch (RuntimeException e) {
        String msg = e.getMessage();
        if (msg != null && msg.startsWith("Room not found")) {
            return ResponseEntity.status(404).body(new ErrorResponse(msg));
        }
        return ResponseEntity.badRequest().body(new ErrorResponse(msg != null ? msg : "Unknown error"));
    }
}

@GetMapping
public List<Booking> getAllBookings() {
return bookingService.getAllBookings();
}

@GetMapping("/{id}")
public ResponseEntity<?> getBookingById(@PathVariable Long id) {
try {
return ResponseEntity.ok(bookingService.getBookingById(id));
} catch (RuntimeException e) {
return ResponseEntity.status(404).body(new ErrorResponse(e.getMessage()));
}
}

@PutMapping("/{id}/status")
public ResponseEntity<?> updateBookingStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
String status = body.get("status");
try {
return ResponseEntity.ok(bookingService.updateBookingStatus(id, status));
} catch (RuntimeException e) {
String msg = e.getMessage();
if (msg != null && msg.contains("not found")) {
return ResponseEntity.status(404).body(new ErrorResponse(msg));
} else {
return ResponseEntity.badRequest().body(new ErrorResponse(msg != null ? msg : "Unknown error"));
}
}
}

@PutMapping("/{id}/payment")
public ResponseEntity<?> processBookingPayment(@PathVariable Long id) {
    try {
        Booking updatedBooking = bookingService.processPayment(id);
        return ResponseEntity.ok(updatedBooking);
    } catch (RuntimeException e) {
        String msg = e.getMessage();
        if (msg != null && msg.contains("not found")) {
            return ResponseEntity.status(404).body(new ErrorResponse(msg));
        } else {
            return ResponseEntity.badRequest().body(new ErrorResponse(msg != null ? msg : "Unknown error"));
        }
    }
}

@DeleteMapping("/{id}")
public ResponseEntity<?> cancelBooking(@PathVariable Long id) {
try {
bookingService.cancelBooking(id);
return ResponseEntity.ok().build();
} catch (RuntimeException e) {
String msg = e.getMessage();
if (msg != null && msg.contains("not found")) {
return ResponseEntity.status(404).body(new ErrorResponse(msg));
} else {
return ResponseEntity.badRequest().body(new ErrorResponse(msg != null ? msg : "Unknown error"));
}
}
}

record ErrorResponse(String message) {}
}