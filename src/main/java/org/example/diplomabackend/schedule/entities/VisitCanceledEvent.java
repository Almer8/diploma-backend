package org.example.diplomabackend.schedule.entities;

import org.springframework.context.ApplicationEvent;

public class VisitCanceledEvent extends ApplicationEvent {
    private final Long visitId;

    public VisitCanceledEvent(Object source, Long visitId) {
        super(source);
        this.visitId = visitId;
    }

    public Long getVisitId() {
        return visitId;
    }
}
