package org.example.diplomabackend.schedule.entities;

import lombok.Data;

import java.util.List;

@Data
public class UpdateServicesRequest {
    private Long id;
    private List<ScheduleEntity.Service> services;

}
