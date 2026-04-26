package com.evently.event_service.repository;

import org.springframework.stereotype.Repository;
import com.evently.event_service.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;


@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    
} 