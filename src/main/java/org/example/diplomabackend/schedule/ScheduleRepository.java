package org.example.diplomabackend.schedule;


import org.example.diplomabackend.schedule.entities.ScheduleEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ScheduleRepository extends MongoRepository<ScheduleEntity, String> {

    ScheduleEntity findByDoctorId(Long id);
    boolean existsByDoctorId(Long id);
}
