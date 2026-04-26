package com.evently.event_service.service;

import org.springframework.stereotype.Service;
import com.evently.event_service.model.Event;
import com.evently.event_service.repository.EventRepository;
import com.evently.event_service.exception.EventNotFoundException;
import java.util.List;

@Service
public class EventService {
    
    private final EventRepository repo;

    public EventService(EventRepository repo) {
        this.repo = repo;
    }

    // Create
    public Event addEvent(Event event) {
        return repo.save(event);
    }

    // Read All
    public List<Event> getAllEvents() {
        return repo.findAll();
    }

    // Read by ID
    public Event getEventById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Event with ID " + id + " not found"));
    }

    // Update
    public Event updateEvent(Long id, Event updated) {
        Event event = getEventById(id);

        event.setName(updated.getName());
        event.setDate(updated.getDate());
        event.setLocation(updated.getLocation());

        return repo.save(event);
    }

    // Delete
    public void deleteEvent(Long id) {
        Event event = getEventById(id);
        repo.delete(event);
    }

}
