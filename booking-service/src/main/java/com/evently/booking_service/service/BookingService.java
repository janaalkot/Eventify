package com.evently.booking_service.service;

import com.evently.booking_service.client.EventServiceClient;
import com.evently.booking_service.client.NotificationServiceClient;
import com.evently.booking_service.dto.CreateBookingRequest;
import com.evently.booking_service.dto.EventResponse;
import com.evently.booking_service.dto.NotificationRequest;
import com.evently.booking_service.entity.Booking;
import com.evently.booking_service.entity.BookingStatus;
import com.evently.booking_service.repository.BookingRepository;
import com.evently.common.security.AuthenticatedUser;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class BookingService {

    private final BookingRepository bookingRepository;
    private final EventServiceClient eventServiceClient;
    private final NotificationServiceClient notificationServiceClient;

    public BookingService(BookingRepository bookingRepository,
                          EventServiceClient eventServiceClient,
                          NotificationServiceClient notificationServiceClient) {
        this.bookingRepository = bookingRepository;
        this.eventServiceClient = eventServiceClient;
        this.notificationServiceClient = notificationServiceClient;
    }

    public Booking createBooking(CreateBookingRequest request, AuthenticatedUser authenticatedUser) {
        EventResponse event = eventServiceClient.getEventById(request.getEventId());
        if (event.getAvailableSeats() == null || event.getAvailableSeats() <= 0) {
            throw new IllegalArgumentException("No available seats remaining for this event");
        }

        try {
            eventServiceClient.reserveSeat(request.getEventId());

            Booking booking = new Booking();
            booking.setUserId(authenticatedUser.id());
            booking.setEventId(request.getEventId());
            booking.setStatus(BookingStatus.CONFIRMED);
            booking.setBookingDate(LocalDateTime.now());
            Booking savedBooking = bookingRepository.save(booking);

            sendNotification(
                    authenticatedUser.id(),
                    "Booking confirmed",
                    "Your booking for \"" + event.getTitle() + "\" has been confirmed."
            );

            return savedBooking;
        } catch (RuntimeException exception) {
            try {
                eventServiceClient.releaseSeat(request.getEventId());
            } catch (Exception ignored) {
            }
            throw exception;
        }
    }

    @Transactional(readOnly = true)
    public List<Booking> getMyBookings(AuthenticatedUser authenticatedUser) {
        return bookingRepository.findByUserId(authenticatedUser.id());
    }

    @Transactional(readOnly = true)
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public void cancelBooking(Long bookingId, AuthenticatedUser authenticatedUser) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found with id " + bookingId));

        if (!authenticatedUser.isAdmin() && !authenticatedUser.id().equals(booking.getUserId())) {
            throw new IllegalArgumentException("You can only cancel your own bookings");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            return;
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        eventServiceClient.releaseSeat(booking.getEventId());

        sendNotification(
                booking.getUserId(),
                "Booking cancelled",
                "Your booking for event ID " + booking.getEventId() + " has been cancelled."
        );
    }

    private void sendNotification(Long userId, String title, String message) {
        try {
            NotificationRequest request = new NotificationRequest();
            request.setUserId(userId);
            request.setTitle(title);
            request.setMessage(message);
            request.setReadStatus(Boolean.FALSE);
            notificationServiceClient.createNotification(request);
        } catch (Exception ignored) {
        }
    }
}
