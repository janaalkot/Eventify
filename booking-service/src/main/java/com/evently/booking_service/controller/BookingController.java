package com.evently.booking_service.controller;

import com.evently.booking_service.dto.CreateBookingRequest;
import com.evently.booking_service.entity.Booking;
import com.evently.booking_service.service.BookingService;
import com.evently.common.security.AuthenticatedUser;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<Booking> createBooking(@Valid @RequestBody CreateBookingRequest request,
                                                 Authentication authentication) {
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookingService.createBooking(request, authenticatedUser));
    }

    @GetMapping("/my")
    public ResponseEntity<List<Booking>> getMyBookings(Authentication authentication) {
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        return ResponseEntity.ok(bookingService.getMyBookings(authenticatedUser));
    }

    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings(Authentication authentication) {
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        if (!authenticatedUser.isAdmin()) {
            throw new IllegalArgumentException("Only admins can view all bookings");
        }
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long id, Authentication authentication) {
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        bookingService.cancelBooking(id, authenticatedUser);
        return ResponseEntity.noContent().build();
    }
}
