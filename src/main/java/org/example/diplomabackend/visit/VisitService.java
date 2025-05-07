package org.example.diplomabackend.visit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.example.diplomabackend.auth.security.CustomUserDetails;
import org.example.diplomabackend.call.CallManagerService;
import org.example.diplomabackend.call.entities.CallStatusResponse;
import org.example.diplomabackend.schedule.ScheduleService;
import org.example.diplomabackend.schedule.entities.VisitCanceledEvent;
import org.example.diplomabackend.userprofile.UserProfileService;
import org.example.diplomabackend.userprofile.entities.UserProfileEntity;
import org.example.diplomabackend.utils.Roles;
import org.example.diplomabackend.visit.entities.CreateVisitRequest;
import org.example.diplomabackend.visit.entities.UpdateVisitRequest;
import org.example.diplomabackend.visit.entities.VisitEntity;
import org.example.diplomabackend.visit.entities.VisitStatus;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class VisitService {

    private final VisitRepository visitRepository;
    private final ScheduleService scheduleService;
    private final UserProfileService userProfileService;
    private final CallManagerService callManagerService;
    private final ModelMapper modelMapper;

    @Value("${liqpay.public_key}")
    String public_key;
    @Value("${liqpay.private_key}")
    String private_key;
    @Value("${liqpay.url}")
    String url;

    @PreAuthorize("hasAuthority('PATIENT') or hasAuthority('DOCTOR')")
    ResponseEntity<?> getVisits(List<VisitStatus> status, Integer page, Integer size, String sortBy, String sortDirection) {
        Sort sort = Sort.unsorted();

        if(sortBy != null && !sortBy.isEmpty()){
            Sort.Direction direction = Sort.Direction.ASC;
            if(sortDirection != null && sortDirection.equalsIgnoreCase("desc")) {
                direction = Sort.Direction.DESC;
            }
            sort = Sort.by(direction, sortBy);
        }
        PageRequest p = PageRequest.of(page, size, sort);

        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(user.getAuthorities().contains(new SimpleGrantedAuthority(Roles.PATIENT.toString()))) {
            return ResponseEntity.ok(visitRepository.findAllByPatientIdAndStatus(user.getId(), status,p));
        }
        if(user.getAuthorities().contains(new SimpleGrantedAuthority(Roles.DOCTOR.toString()))) {
            return ResponseEntity.ok(visitRepository.findAllByDoctorIdAndStatus(user.getId(), status,p));
        }
        throw new RuntimeException("Access denied");
    }

    @PreAuthorize("hasAuthority('PATIENT') or hasAuthority('DOCTOR')")
    ResponseEntity<?> createVisit(CreateVisitRequest r) {



        VisitEntity visit = VisitEntity.create(r);
        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.getId().equals(visit.getDoctorId()) && !user.getId().equals(visit.getPatientId())) {
            throw new RuntimeException("Access denied");
        }

        VisitEntity savedEntity = visitRepository.save(visit);
        try {
            scheduleService.createVisit(savedEntity.getId(), savedEntity.getDoctorId(), savedEntity.getStartTime(), savedEntity.getEndTime());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(savedEntity);
        }

    @PreAuthorize("hasAuthority('PATIENT') or hasAuthority('DOCTOR')")
    ResponseEntity<?> deleteVisit(Long id){
        VisitEntity visit = visitRepository.findById(id).orElseThrow();
        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.getId().equals(visit.getDoctorId()) && !user.getId().equals(visit.getPatientId())) {
            throw new RuntimeException("Access denied");
        }
        if(scheduleService.deleteVisit(visit.getId(),visit.getDoctorId(),visit.getStartTime())){
            return ResponseEntity.ok("Visit deleted");
        } else {
            return ResponseEntity.badRequest().body("Visit not deleted");
        }
    }
    @EventListener
    void deleteVisitEvent(VisitCanceledEvent e){
            VisitEntity visit = visitRepository.findById(e.getVisitId()).orElseThrow();
        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.getId().equals(visit.getDoctorId()) && !user.getId().equals(visit.getPatientId())) {
            throw new RuntimeException("Access denied");
        }
        visit.setStatus(VisitStatus.CANCELED);
        visitRepository.save(visit);
    }


    @PreAuthorize("hasAuthority('DOCTOR') or hasAuthority('PATIENT')")
    ResponseEntity<?> connectToVisit(Long id){
        VisitEntity visit = visitRepository.findById(id).orElseThrow();
        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.getId().equals(visit.getDoctorId()) && !user.getId().equals(visit.getPatientId())) {
            throw new RuntimeException("Access denied");
        }
       // CallStatusResponse response = callManagerService.handleConnectionAttempt(visit, user.getId());
        return ResponseEntity.ok().build();
    }


    ResponseEntity<?> generatePayLink(Long id) {

        VisitEntity visit = visitRepository.findById(id).orElseThrow();
        if(!visit.getStatus().equals(VisitStatus.PLANNED)){
            throw new RuntimeException("This visit can't be payed");
        }

        UserProfileEntity doctor = userProfileService.getUserProfile(visit.getDoctorId());

        String formattedName = String.format("%s %s%s",
                doctor.getSurname(),
                doctor.getName() != null && !doctor.getName().isEmpty() ? doctor.getName().charAt(0) + "." : "",
                (doctor.getPatronymic() != null && !doctor.getPatronymic().isEmpty() ? " " + doctor.getPatronymic().charAt(0) + "." : "")
        );

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode();
        json.put("version","3");
        json.put("public_key",public_key);
        json.put("action", "pay");
        json.put("amount", visit.getPrice().toString());
        json.put("currency", "UAH");
        json.put("description", String.format("Оплата послуги %s від %s у лікаря %s",
                visit.getService(),
                visit.getStartTime().toLocalDateTime().toLocalDate(),
                formattedName));
        json.put("order_id",  String.format("%s/%s", visit.getId(), LocalDateTime.now()));
        String data = Base64.getEncoder().encodeToString(json.toString().getBytes());

        String sign_string = private_key + data + private_key;
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            return ResponseEntity.badRequest().body("No support for SHA-1");
        }
        byte[] hashBytes = md.digest(sign_string.getBytes(StandardCharsets.UTF_8));

        String signature = Base64.getEncoder().encodeToString(hashBytes);

        Map<String, String> response = new HashMap<>();
        response.put("data", data);
        response.put("signature", signature);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('DOCTOR') or hasAuthority('PATIENT')") //TODO Remove Patient Access
    ResponseEntity<?> updateVisit(UpdateVisitRequest r){
        VisitEntity visit = visitRepository.findById(r.getId()).orElseThrow();
        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.getId().equals(visit.getDoctorId()) && !user.getId().equals(visit.getPatientId())) {
            throw new RuntimeException("Access denied");
        }
        modelMapper.map(r,visit);
        if(r.getDiagnosis() != null){
            visit.setDiagnosis(r.getDiagnosis());
        }

        return ResponseEntity.ok(visitRepository.save(visit));
    }

    public VisitEntity getVisit(Long id){
        return visitRepository.findById(id).orElseThrow();
    }

}

