package org.example.diplomabackend.diagnosis.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.util.List;

@Value
public class DiagnosisResponse {

    @JsonProperty("_embedded")
    Embedded embedded;

    @Value
    private static class Embedded{
        List<DiagnosisEntity> diagnosis;
    }
    public DiagnosisResponse(List<DiagnosisEntity> diagnosis) {
        this.embedded = new Embedded(diagnosis);
    }

}
