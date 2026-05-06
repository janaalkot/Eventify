package com.evently.event_service.service;

import com.evently.common.security.AuthenticatedUser;
import com.evently.event_service.dto.EventRequest;
import com.evently.event_service.entity.Event;
import com.evently.event_service.repository.EventRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Transactional(readOnly = true)
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id " + id));
    }

    public Event createEvent(EventRequest request, AuthenticatedUser authenticatedUser) {
        validateDates(request);

        Event event = new Event();
        event.setTitle(request.getTitle().trim());
        event.setDescription(request.getDescription().trim());
        event.setLocation(request.getLocation().trim());
        event.setStartDateTime(request.getStartDateTime());
        event.setEndDateTime(request.getEndDateTime());
        event.setCapacity(request.getCapacity());
        event.setAvailableSeats(request.getCapacity());
        event.setCategory(request.getCategory().trim());
        event.setCreatedByAdminId(authenticatedUser.id());
        return eventRepository.save(event);
    }

    public Event updateEvent(Long id, EventRequest request) {
        validateDates(request);

        Event event = getEventById(id);
        int bookedSeats = event.getCapacity() - event.getAvailableSeats();
        if (request.getCapacity() < bookedSeats) {
            throw new IllegalArgumentException("Capacity cannot be less than current confirmed bookings");
        }

        event.setTitle(request.getTitle().trim());
        event.setDescription(request.getDescription().trim());
        event.setLocation(request.getLocation().trim());
        event.setStartDateTime(request.getStartDateTime());
        event.setEndDateTime(request.getEndDateTime());
        event.setCategory(request.getCategory().trim());
        event.setCapacity(request.getCapacity());
        event.setAvailableSeats(request.getCapacity() - bookedSeats);
        return eventRepository.save(event);
    }

    public void deleteEvent(Long id) {
        Event event = getEventById(id);
        eventRepository.delete(event);
    }

    public Event reserveSeat(Long id) {
        Event event = eventRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id " + id));

        if (event.getAvailableSeats() <= 0) {
            throw new IllegalArgumentException("No available seats remaining for this event");
        }

        event.setAvailableSeats(event.getAvailableSeats() - 1);
        return eventRepository.save(event);
    }

    public Event releaseSeat(Long id) {
        Event event = eventRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id " + id));

        if (event.getAvailableSeats() >= event.getCapacity()) {
            return event;
        }

        event.setAvailableSeats(event.getAvailableSeats() + 1);
        return eventRepository.save(event);
    }

    private void validateDates(EventRequest request) {
        if (!request.getEndDateTime().isAfter(request.getStartDateTime())) {
            throw new IllegalArgumentException("End date must be after start date");
        }
    }
}
