package org.example.diplomabackend.diagnosis;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/diagnosis")
public class DiagnosisController {

    private final DiagnosisService diagnosisService;

    @GetMapping("/{q}")
    public ResponseEntity<?> getDiagnosis(@PathVariable String q) {
       return diagnosisService.getDiagnosis(q);
    }
}
