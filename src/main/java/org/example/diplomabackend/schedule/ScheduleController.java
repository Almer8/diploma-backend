package org.example.diplomabackend.schedule;

import lombok.RequiredArgsConstructor;
import org.example.diplomabackend.schedule.entities.DayStatusChangeRequest;
import org.example.diplomabackend.schedule.entities.UpdateTemplateRequest;
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

}
