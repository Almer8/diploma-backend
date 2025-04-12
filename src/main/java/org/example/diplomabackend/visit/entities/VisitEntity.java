package org.example.diplomabackend.visit.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "visits")
@Builder
public class VisitEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(nullable = false, name = "doctor_id")
    private Long doctorId;
    @Column(nullable = false, name = "patient_id")
    private Long patientId;
    @Column(nullable = false, name = "start_time")
    private Timestamp startTime;
    @Column(nullable = false, name = "end_time")
    private Timestamp endTime;
    @Column(nullable = false)
    private String service;
    private BigDecimal price;
    private VisitStatus status;
    @ElementCollection
    private List<Long> diagnosis;
    private String recommendations;

    public static VisitEntity create(CreateVisitRequest r){
        return VisitEntity.builder()
                .doctorId(r.getDoctorId())
                .patientId(r.getPatientId())
                .startTime(r.getStartTime())
                .endTime(r.getEndTime())
                .service(r.getService())
                .price(r.getPrice())
                .status(VisitStatus.PLANNED)
                .build();
    }
}
