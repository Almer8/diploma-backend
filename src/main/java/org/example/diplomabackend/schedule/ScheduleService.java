package org.example.diplomabackend.schedule;

import lombok.RequiredArgsConstructor;
import org.example.diplomabackend.schedule.entities.ScheduleEntity;
import org.example.diplomabackend.schedule.entities.UpdateTemplateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public ScheduleEntity getScheduleByDoctorId(Long id) {
        return scheduleRepository.findByDoctorId(id);
    }

    @PreAuthorize("hasAuthority('DOCTOR') and @decider.tokenIdEqualsIdFromRequest(#id)")
    public ScheduleEntity createSchedule(Long id){
        if(scheduleRepository.existsByDoctorId(id)){
            throw new RuntimeException("Schedule already exists");
        }
        ScheduleEntity schedule = new ScheduleEntity();
        schedule.setDoctorId(id);
        schedule.setIsWorking(false);
        return scheduleRepository.save(schedule);
    }

    @PreAuthorize("hasAuthority('DOCTOR') and @decider.tokenIdEqualsIdFromRequest(#r.id)")
    public ResponseEntity<?> updateTemplate(UpdateTemplateRequest r){
        ScheduleEntity schedule = getScheduleByDoctorId(r.getId());
        schedule.setTemplate(r.getTemplate());
        return ResponseEntity.ok().body(scheduleRepository.save(schedule));
    }

    public ResponseEntity<?> createVisit(Long appointmentId, Long doctorId, java.sql.Timestamp start, java.sql.Timestamp end){
        ScheduleEntity scheduleEntity = getScheduleByDoctorId(doctorId);
        LocalDate visitDate = start.toLocalDateTime().toLocalDate();
        List<ScheduleEntity.ScheduleDay> days = scheduleEntity.getSchedule();

        ScheduleEntity.ScheduleDay scheduleDay;

        Optional<ScheduleEntity.ScheduleDay> dayFetch = days.stream()
                .filter(e -> e.getDate().equals(visitDate))
                .findFirst();

        if(dayFetch.isEmpty()){
            Optional<ScheduleEntity.ScheduleTemplate> template = scheduleEntity.getTemplate().stream()
                    .filter(e -> e.getDay().equalsIgnoreCase(visitDate.getDayOfWeek().toString().toLowerCase()))
                    .findFirst();

            if(template.isEmpty()){
                throw new RuntimeException("Schedule template not found");
            }

            scheduleDay = new ScheduleEntity.ScheduleDay(visitDate, ScheduleEntity.Slot.copy(template.get().getSlots()), true);
            days.add(scheduleDay);
        } else {
            scheduleDay = dayFetch.get();
            if (!scheduleDay.getIs_working()){
                throw new RuntimeException("Schedule day is not working day");
            }
        }

        List<ScheduleEntity.Slot> slots = scheduleDay.getSlots();

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String startTime = start.toLocalDateTime().toLocalTime().format(timeFormatter);
        String endTime = end.toLocalDateTime().toLocalTime().format(timeFormatter);

        Optional<ScheduleEntity.Slot> scheduleSlotSearch = slots.stream()
                .filter(e -> e.getStart().equals(startTime))
                .findFirst();

        if(scheduleSlotSearch.isEmpty()){
            throw new RuntimeException("Schedule slot not found");
        }

        ScheduleEntity.Slot scheduleSlot = scheduleSlotSearch.get();

        if(!scheduleSlot.getEnd().equals(endTime)){
            throw new RuntimeException("Schedule slot end time not match");
        }

        if(scheduleSlot.isTaken()){
            throw new RuntimeException("This slot is already taken");
        }

        scheduleSlot.setStart(startTime);
        scheduleSlot.setEnd(endTime);
        scheduleSlot.setTaken(true);
        scheduleSlot.setAppointment_id(appointmentId);

        return ResponseEntity.ok(scheduleRepository.save(scheduleEntity));
    }

    public ResponseEntity<?> deleteVisit(Long appointmentId, Long doctorId, java.sql.Timestamp start){
        ScheduleEntity scheduleEntity = getScheduleByDoctorId(doctorId);
        LocalDate visitDate = start.toLocalDateTime().toLocalDate();
        List<ScheduleEntity.ScheduleDay> days = scheduleEntity.getSchedule();

        ScheduleEntity.ScheduleDay day = days.stream()
                .filter(e -> e.getDate().equals(visitDate))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Schedule day not found"));

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String startTime = start.toLocalDateTime().toLocalTime().format(timeFormatter);

        Optional<ScheduleEntity.Slot> slot = day.getSlots().stream()
                .filter(e -> e.getStart().equals(startTime))
                .findFirst();

        if(slot.isPresent() && slot.get().getAppointment_id().equals(appointmentId)){
            slot.get().setTaken(false);
            slot.get().setAppointment_id(null);
            scheduleRepository.save(scheduleEntity);
            return ResponseEntity.ok(true);
        }

        return ResponseEntity.ok(false);
    }

    @PreAuthorize("hasAuthority('DOCTOR') and @decider.tokenIdEqualsIdFromRequest(#doctorId)")
    public ResponseEntity<?> setDayWorkingStatus(LocalDate date, Long doctorId, boolean isWorking) {
        ScheduleEntity scheduleEntity = getScheduleByDoctorId(doctorId);
        List<ScheduleEntity.ScheduleDay> days = scheduleEntity.getSchedule();

        Optional<ScheduleEntity.ScheduleDay> dayFetch = days.stream()
                .filter(e -> e.getDate().equals(date))
                .findFirst();

        if (dayFetch.isPresent()) {
            ScheduleEntity.ScheduleDay scheduleDay = dayFetch.get();
            scheduleDay.setIs_working(isWorking);

            if (!isWorking) {

                for (ScheduleEntity.Slot slot : scheduleDay.getSlots()) {
                    if (slot.isTaken()) {
                        Long id = slot.getAppointment_id();
                        slot.setTaken(false);
                        slot.setAppointment_id(null);
                        // TODO: Send event to Visits Service to cancel the visit with this id
                    }
                }
            }
        } else {
            if (!isWorking) {
                Optional<ScheduleEntity.ScheduleTemplate> template = scheduleEntity.getTemplate().stream()
                        .filter(t -> t.getDay().equalsIgnoreCase(date.getDayOfWeek().toString().toLowerCase()))
                        .findFirst();

                if (template.isEmpty()) {
                    throw new RuntimeException("Schedule template not found");
                }

                ScheduleEntity.ScheduleDay newDay = new ScheduleEntity.ScheduleDay(
                        date,
                        ScheduleEntity.Slot.copy(template.get().getSlots()),
                        false
                );
                days.add(newDay);
            } else {
                return ResponseEntity.ok("No change needed");
            }
        }

        return ResponseEntity.ok(scheduleRepository.save(scheduleEntity));
    }
}