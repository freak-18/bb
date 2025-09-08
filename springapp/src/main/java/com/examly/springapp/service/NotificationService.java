package com.examly.springapp.service;

import com.examly.springapp.model.Booking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${zenstay.admin.email}")
    private String adminEmail;

    @Async
    public void sendBookingConfirmation(Booking booking) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(booking.getGuestEmail());
            message.setSubject("Booking Confirmation - #" + booking.getBookingId());
            message.setText("Dear " + booking.getGuestName() + ",\n\n"
                + "Thank you for your booking at ZENStay. Your booking is currently PENDING and awaiting confirmation.\n\n"
                + "Booking Details:\n"
                + "Room: " + booking.getRoom().getRoomType() + " (Room " + booking.getRoom().getRoomNumber() + ")\n"
                + "Check-in: " + booking.getCheckInDate() + "\n"
                + "Check-out: " + booking.getCheckOutDate() + "\n"
                + "Total Price: \u20b9" + booking.getTotalPrice()
                + "\n\nWe will notify you once your booking is approved.\n\nBest regards,\nThe ZENStay Team");
            mailSender.send(message);
            sendAdminNotification("New Booking Created", "A new booking has been created: #" + booking.getBookingId() + " by " + booking.getGuestName());
            logger.info("Booking confirmation email sent to {}", booking.getGuestEmail());
        } catch (MailException e) {
            logger.error("Failed to send booking confirmation email to {}: {}", booking.getGuestEmail(), e.getMessage());
        }
    }

    @Async
    public void sendBookingStatusUpdate(Booking booking) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(booking.getGuestEmail());
            message.setSubject("Booking Status Update - #" + booking.getBookingId());
            message.setText("Dear " + booking.getGuestName() + ",\n\n"
                + "The status of your booking #" + booking.getBookingId() + " has been updated to: " + booking.getStatus() + ".\n\n"
                + "Thank you for choosing ZENStay.\n\nBest regards,\nThe ZENStay Team");
            mailSender.send(message);
            sendAdminNotification("Booking Status Updated", "The status for booking #" + booking.getBookingId() + " has been updated to " + booking.getStatus() + ".");
            logger.info("Booking status update email sent to {}", booking.getGuestEmail());
        } catch (MailException e) {
            logger.error("Failed to send booking status update email to {}: {}", booking.getGuestEmail(), e.getMessage());
        }
    }

    private void sendAdminNotification(String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(adminEmail);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            logger.info("Admin notification sent for: {}", subject);
        } catch (MailException e) {
            logger.error("Failed to send admin notification for {}: {}", subject, e.getMessage());
        }
    }
}