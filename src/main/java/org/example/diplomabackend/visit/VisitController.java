package org.example.diplomabackend.visit;

import lombok.RequiredArgsConstructor;
import org.example.diplomabackend.visit.entities.CreateVisitRequest;
import org.example.diplomabackend.visit.entities.UpdateVisitRequest;
import org.example.diplomabackend.visit.entities.VisitStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/visit")
public class VisitController {

    private final VisitService visitService;

    @GetMapping
    public ResponseEntity<?> getVisits(
            @RequestParam(name = "status", required = false) List<VisitStatus> status,
            @RequestParam(name = "page") Integer page,
            @RequestParam(name = "size") Integer size,
            @RequestParam(name = "sortBy", defaultValue = "startTime") String sortBy,
            @RequestParam(name = "sortDirection", defaultValue = "ASC") String sortDirection
    ){
        return visitService.getVisits(status, page, size, sortBy, sortDirection);
    }
    @GetMapping("/pay/{id}")
    public ResponseEntity<?> payVisit(@PathVariable Long id){
        return visitService.generatePayLink(id);
    }
    @GetMapping("/connect/{id}")
    public ResponseEntity<?> connectToVisit(@PathVariable Long id){
        return visitService.connectToVisit(id);
    }
    @PostMapping
    public ResponseEntity<?> createVisit(@RequestBody CreateVisitRequest r){
        return visitService.createVisit(r);
    }
    @PatchMapping
    public ResponseEntity<?> updateVisit(@RequestBody UpdateVisitRequest r){return visitService.updateVisit(r);}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVisit(@PathVariable Long id){
        return visitService.deleteVisit(id);
    }

}
