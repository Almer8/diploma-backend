package org.example.diplomabackend.diagnosis;

import org.example.diplomabackend.diagnosis.entities.DiagnosisEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiagnosisRepository extends JpaRepository<DiagnosisEntity, Long> {

    @Query("SELECT d from diagnosis d " +
            "WHERE LOWER(d.icd_10_code) LIKE concat('%',:q,'%') OR LOWER(d.name) LIKE concat('%',:q,'%')")
    List<DiagnosisEntity> getAllByQuery(String q);
}
