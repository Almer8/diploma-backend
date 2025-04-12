package org.example.diplomabackend.schedule.entities;

import lombok.Value;

import java.time.LocalDate;

@Value
public class UpdateVacationRequest {
    Long id;
    LocalDate start;
    LocalDate end;
}
