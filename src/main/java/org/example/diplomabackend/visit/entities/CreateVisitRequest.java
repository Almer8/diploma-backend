package org.example.diplomabackend.visit.entities;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class CreateVisitRequest {

    private Long doctorId;
    private Long patientId;
    private Timestamp startTime;
    private Timestamp endTime;
    private String service;
    private BigDecimal price;
}
