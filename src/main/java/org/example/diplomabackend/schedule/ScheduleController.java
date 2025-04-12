package org.example.diplomabackend.schedule;

import lombok.RequiredArgsConstructor;
import org.example.diplomabackend.schedule.entities.DayStatusChangeRequest;
import org.example.diplomabackend.schedule.entities.UpdateServicesRequest;
import org.example.diplomabackend.schedule.entities.UpdateTemplateRequest;
import org.example.diplomabackend.schedule.entities.UpdateVacationRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;


    @PatchMapping("/template")
    ResponseEntity<?> updateTemplate(@RequestBody UpdateTemplateRequest e) {
        return scheduleService.updateTemplate(e);
    }
    @PatchMapping("/day-status/{id}")
    ResponseEntity<?> weekend(@PathVariable Long id, @RequestBody DayStatusChangeRequest r) {
        return scheduleService.setDayWorkingStatus(r.getDate(), id,r.getWorking());
    }
    @PatchMapping("/service")
    ResponseEntity<?> updateServices(@RequestBody UpdateServicesRequest r) {
        return scheduleService.updateServices(r);
    }
    @PatchMapping("/vacation")
    ResponseEntity<?> updateVacation(@RequestBody UpdateVacationRequest r) {
        return scheduleService.updateVacation(r);
    }


}
