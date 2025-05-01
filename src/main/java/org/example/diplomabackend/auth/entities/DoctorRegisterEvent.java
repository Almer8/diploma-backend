package org.example.diplomabackend.auth.entities;

import org.springframework.context.ApplicationEvent;

public class DoctorRegisterEvent extends ApplicationEvent {
    private final Long id;

    public DoctorRegisterEvent(Object source, Long id) {
        super(source);
        this.id = id;
    }
    public Long getId() {
        return id;
    }
}
