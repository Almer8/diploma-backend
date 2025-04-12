package org.example.diplomabackend.visit;

import org.example.diplomabackend.visit.entities.VisitEntity;
import org.example.diplomabackend.visit.entities.VisitStatus;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VisitRepository extends JpaRepository<VisitEntity, Long> {

    @Query("SELECT v FROM visits v WHERE " +
            "v.doctorId = :id AND " +
            "(:status IS NULL OR v.status = :status)")
    List<VisitEntity> findAllByDoctorIdAndStatus(@Param("id") Long doctorId,@Param("status") VisitStatus status, PageRequest p);
    @Query("SELECT v FROM visits v WHERE " +
            "v.patientId = :id AND " +
            "(:status IS NULL OR v.status = :status)")
    List<VisitEntity> findAllByPatientIdAndStatus(Long patientId, VisitStatus status, PageRequest p);
}
