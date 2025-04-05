package org.example.diplomabackend.anonstoryreport;

import lombok.RequiredArgsConstructor;
import org.example.diplomabackend.anonstoryreport.entities.CreateAnonStoryReportRequest;
import org.example.diplomabackend.anonstoryreport.entities.UpdateAnonStoryReportRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/story/report")
public class AnonStoryReportController {
    private final AnonStoryReportService anonStoryReportService;

    @GetMapping
    public ResponseEntity<?> getStoryReports(
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size,
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam("sortDirection") String sortDirection) {

        return anonStoryReportService.getAnonStoryReports(page, size, sortBy, sortDirection);

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getStoryReportById(@PathVariable("id") Long id) {

        return anonStoryReportService.getAnonStoryReport(id);

    }

    @PostMapping
    public ResponseEntity<?> createStoryReport(@RequestBody CreateAnonStoryReportRequest r) {
        return anonStoryReportService.createAnonStoryReport(r);
    }

    @PutMapping
    public ResponseEntity<?> updateStoryReport(@RequestBody UpdateAnonStoryReportRequest r) {
        return anonStoryReportService.updateAnonStoryReport(r);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStoryReport(@PathVariable("id") Long id) {
        return anonStoryReportService.deleteAnonStoryReport(id);
    }



}
