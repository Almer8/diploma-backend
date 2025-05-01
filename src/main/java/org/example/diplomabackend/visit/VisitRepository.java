package org.example.diplomabackend.visit;

import org.example.diplomabackend.visit.entities.VisitEntity;
import org.example.diplomabackend.visit.entities.VisitStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VisitRepository extends JpaRepository<VisitEntity, Long> {

    @Query("SELECT v FROM visits v WHERE " +
            "v.doctorId = :id AND " +
            "(:statuses IS NULL OR v.status IN (:statuses))")
    Page<VisitEntity> findAllByDoctorIdAndStatus(@Param("id") Long doctorId,@Param("statuses") List<VisitStatus> statuses, PageRequest p);
    @Query("SELECT v FROM visits v WHERE " +
            "v.patientId = :id AND " +
            "(:statuses IS NULL OR v.status IN (:statuses))")
    Page<VisitEntity> findAllByPatientIdAndStatus(@Param("id") Long patientId, @Param("statuses") List<VisitStatus> statuses, PageRequest p);
}
