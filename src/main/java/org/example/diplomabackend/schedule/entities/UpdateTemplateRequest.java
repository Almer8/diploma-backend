package org.example.diplomabackend.schedule.entities;

import lombok.Value;

import java.util.List;

@Value
public class UpdateTemplateRequest {
    Long id;
    List<ScheduleEntity.ScheduleTemplate> template;
}
