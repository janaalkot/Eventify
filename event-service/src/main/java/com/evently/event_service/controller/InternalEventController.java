package com.evently.event_service.controller;

import com.evently.event_service.entity.Event;
import com.evently.event_service.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/_internal/events")
public class InternalEventController {

    private final EventService eventService;

    public InternalEventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/{id}/reserve")
    public ResponseEntity<Event> reserveSeat(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.reserveSeat(id));
    }

    @PostMapping("/{id}/release")
    public ResponseEntity<Event> releaseSeat(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.releaseSeat(id));
    }
}
