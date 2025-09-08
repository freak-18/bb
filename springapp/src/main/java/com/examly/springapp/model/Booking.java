package com.examly.springapp.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    private String guestName;
    private String guestEmail;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Double totalPrice;
    private String status; // PENDING, APPROVED, REJECTED
    private LocalDateTime createdAt;
    
    public Room getRoom() {
        return room;
    }
    
    public String getGuestEmail() {
        return guestEmail;
    }
    
    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }
    
    public LocalDate getCheckInDate() {
        return checkInDate;
    }
    
    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getStatus() {
        return status;
    }
    
    public Long getBookingId() {
        return bookingId;
    }
    
    public String getGuestName() {
        return guestName;
    }
    
    public Double getTotalPrice() {
        return totalPrice;
    }
    
    public void setRoom(Room room) {
        this.room = room;
    }
    
    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }
    
    public void setGuestEmail(String guestEmail) {
        this.guestEmail = guestEmail;
    }
    
    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }
    
    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }
}
