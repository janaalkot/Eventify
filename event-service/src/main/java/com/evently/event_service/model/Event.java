package com.evently.event_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Entity
public class Event {
    
    @Id //marks this field as the primary key of the entity, each event will have a unique id that can be used to identify it in the database
    @GeneratedValue(strategy = GenerationType.IDENTITY) //configures the id to be generated automatically by the database when a new event is created, using the identity strategy which relies on auto-incrementing columns in the database
    private Long id;

    @NotBlank(message = "Name is required") //@NotBlank can be used with strings only
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    private String name;

    @NotNull(message = "Date is required") //ensures that the date field is not null, @NotNull can be used with any type of object
    private LocalDate date;

    @NotBlank(message = "Location is required")
    @Size(min = 2, message = "Location must be at least 2 characters")
    private String location;

    public Event() {  //important for objects creation from JSON in spring boot especially when using @RequestBody in controllers
    }

    public Event(Long id, String name, LocalDate date, String location) { //used when creating event objects manually in tests or other parts of the code
        this.id = id;
        this.name = name;
        this.date = date;
        this.location = location;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    
    public LocalDate getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() { //transfrorms the event object to a string representation for easy printing in console and logs
        return "Event{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", date='" + date + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
