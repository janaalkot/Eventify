package com.evently.booking_service.dto;

import jakarta.validation.constraints.NotNull;

public class CreateBookingRequest {

    @NotNull(message = "Event id is required")
    private Long eventId;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
}
