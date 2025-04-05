package org.example.diplomabackend.anonstoryreport;

import org.example.diplomabackend.anonstoryreport.entities.AnonStoryReportEntity;
import org.example.diplomabackend.anonstoryreport.entities.AnonStoryReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnonStoryReportRepository extends JpaRepository<AnonStoryReportEntity,Long> {
    Page<AnonStoryReportEntity> findAllByStatus(PageRequest pageRequest, AnonStoryReportStatus status);
    void deleteAllByStoryId(Long id);
}
