package org.example.diplomabackend.visit.entities;

import lombok.Data;

import java.util.List;

@Data
public class UpdateVisitRequest {

    Long id;
    List<Long> diagnosis;
    String recommendations;
}
