package com.evently.booking_service.client;

import com.evently.booking_service.dto.EventResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "event-service")
public interface EventServiceClient {

    @GetMapping("/events/{id}")
    EventResponse getEventById(@PathVariable("id") Long id);

    @PostMapping("/_internal/events/{id}/reserve")
    EventResponse reserveSeat(@PathVariable("id") Long id);

    @PostMapping("/_internal/events/{id}/release")
    EventResponse releaseSeat(@PathVariable("id") Long id);
}
