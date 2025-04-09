package org.example.diplomabackend.schedule.entities;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DayStatusChangeRequest {
    private LocalDate date;
    private Boolean working;
}
