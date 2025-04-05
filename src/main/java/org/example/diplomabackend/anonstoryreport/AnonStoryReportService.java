package org.example.diplomabackend.anonstoryreport;

import lombok.RequiredArgsConstructor;
import org.example.diplomabackend.anonstoryreport.entities.AnonStoryReportEntity;
import org.example.diplomabackend.anonstoryreport.entities.AnonStoryReportStatus;
import org.example.diplomabackend.anonstoryreport.entities.CreateAnonStoryReportRequest;
import org.example.diplomabackend.anonstoryreport.entities.UpdateAnonStoryReportRequest;
import org.example.diplomabackend.auth.security.CustomUserDetails;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AnonStoryReportService {
    private final AnonStoryReportRepository anonStoryReportRepository;
    private final ModelMapper modelMapper;

    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> getAnonStoryReports(Integer page, Integer size, String sortBy, String sortDirection) {
        Sort sort = Sort.unsorted();

        if(sortBy != null && !sortBy.isEmpty()){
            Sort.Direction direction = Sort.Direction.ASC;
            if(sortDirection != null && sortDirection.equalsIgnoreCase("desc")) {
                direction = Sort.Direction.DESC;
            }
            sort = Sort.by(direction, sortBy);
        }
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Page<AnonStoryReportEntity> reports = anonStoryReportRepository.findAllByStatus(pageRequest, AnonStoryReportStatus.CREATED);
        return ResponseEntity.ok(reports);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> getAnonStoryReport(Long id) {
        return ResponseEntity.ok(anonStoryReportRepository.findById(id).orElseThrow(()-> new RuntimeException("This report doesn't exist")));
    }

    @PreAuthorize("hasAuthority('PATIENT')")
    public ResponseEntity<?> createAnonStoryReport(CreateAnonStoryReportRequest r) {
        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        AnonStoryReportEntity anonStoryReport = AnonStoryReportEntity.create(r,user.getId());
        return ResponseEntity.ok(anonStoryReportRepository.save(anonStoryReport));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> updateAnonStoryReport(UpdateAnonStoryReportRequest r) {
        AnonStoryReportEntity anonStoryReport = anonStoryReportRepository.findById(r.getId()).orElseThrow(()-> new RuntimeException("This report doesn't exist"));
        modelMapper.map(r, anonStoryReport);

        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        anonStoryReport.setAdmin_id(user.getId());

        return ResponseEntity.ok(anonStoryReportRepository.save(anonStoryReport));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> deleteAnonStoryReport(Long id) {
        AnonStoryReportEntity anonStoryReport = anonStoryReportRepository.findById(id).orElseThrow(()-> new RuntimeException("This report doesn't exist"));
        anonStoryReportRepository.delete(anonStoryReport);
        return ResponseEntity.ok("Report deleted");
    }

    public void deleteAnonStoryReportsByStoryId(Long id){
        anonStoryReportRepository.deleteAllByStoryId(id);
    }

}
