package org.example.diplomabackend.schedule.entities;

import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.example.diplomabackend.utils.DoctorCategories;
import org.example.diplomabackend.utils.DoctorRoles;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "doctor_schedules")
public class ScheduleEntity {

    @Id
    private String id;
    @Field(name = "doctor_id")
    private Long doctorId;
    @Version
    private Long version;
    @Field(name = "is_working")
    private Boolean isWorking;
    @Field(name = "vacation_from")
    private LocalDate vacationFrom;
    @Field(name = "vacation_to")
    private LocalDate vacationTo;
    private DoctorRoles role;
    private List<DoctorCategories> categories;
    private String country;
    private List<Service> services;
    private List<ScheduleTemplate> template;
    private List<ScheduleDay> schedule;

    public ScheduleEntity() {
        isWorking = false;
        vacationFrom = null;
        vacationTo = null;
        role = null;
        services = new ArrayList<>();
        template = new ArrayList<>();
        schedule = new ArrayList<>();
    }

    @Data
    public static class ScheduleTemplate {
        private String day;
        private List<Slot> slots;
    }

    @Data
    @AllArgsConstructor
    public static class ScheduleDay {
        private LocalDate date;
        private List<Slot> slots;
        private Boolean is_working;
    }

    @Data
    public static class Service {
        private String name;
        private BigDecimal price;
    }

    @Data
    @Builder
    public static class Slot {
        private String start;
        private String end;
        private Boolean taken;
        private Long appointment_id;

        public Boolean isTaken() {
            return taken;
        }

        public static List<Slot> copy(List<Slot> slots) {
            return slots.stream()
                    .map(s -> Slot.builder()
                            .start(s.start)
                            .end(s.end)
                            .taken(s.taken)
                            .build()).toList();
        }
    }
}