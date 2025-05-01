package org.example.diplomabackend.auth.entities;

import org.springframework.context.ApplicationEvent;

public class UserDeleteEvent extends ApplicationEvent {
    private final Long id;

    public UserDeleteEvent(Object source, Long id) {
        super(source);
        this.id = id;
    }
    public Long getId() {
        return id;
    }
}