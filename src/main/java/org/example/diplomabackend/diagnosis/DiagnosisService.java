package org.example.diplomabackend.diagnosis;

import lombok.RequiredArgsConstructor;
import org.example.diplomabackend.diagnosis.entities.DiagnosisResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class DiagnosisService {

    private final DiagnosisRepository diagnosisRepository;

    @PreAuthorize("hasAuthority('DOCTOR')")
    ResponseEntity<?> getDiagnosis(String q) {
        return ResponseEntity.ok(new DiagnosisResponse(diagnosisRepository.getAllByQuery(q.toLowerCase())));
    }
    @PreAuthorize("hasAuthority('DOCTOR') or hasAuthority('PATIENT')")
    ResponseEntity<?> getDiagnosisListByIds(List<Long> ids) {
        return ResponseEntity.ok(new DiagnosisResponse(diagnosisRepository.findAllById(ids)));
    }
}
