package org.example.diplomabackend.userprofile;

import lombok.RequiredArgsConstructor;
import org.example.diplomabackend.auth.entities.AuthService;
import org.example.diplomabackend.auth.entities.UpdateRequest;
import org.example.diplomabackend.auth.entities.UserDeleteEvent;
import org.example.diplomabackend.auth.security.CustomUserDetails;
import org.example.diplomabackend.auth.entities.UserRegisterEvent;
import org.example.diplomabackend.schedule.ScheduleService;
import org.example.diplomabackend.userprofile.entities.ExtendedProfileResponse;
import org.example.diplomabackend.userprofile.entities.UserProfileEntity;
import org.example.diplomabackend.userprofile.entities.UserProfileResponse;
import org.example.diplomabackend.userprofile.entities.UserProfileUpdateRequest;
import org.example.diplomabackend.utils.Roles;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final AuthService authService;
    private final ModelMapper modelMapper;
    private final ScheduleService scheduleService;

    @Value("${filestorage.directory}")
    private String UPLOAD_DIRECTORY;

    ResponseEntity<?> getUsers(Roles role, String q, Integer page, Integer size, String sortBy, String sortDirection) {

        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Sort sort;
        Sort.Direction direction = Sort.Direction.ASC;
        if (sortDirection.equalsIgnoreCase("desc")) {
            direction = Sort.Direction.DESC;
        }

        sort = Sort.by(direction, sortBy);
        PageRequest p = PageRequest.of(page, size, sort);
        Page<Object[]> resultPage;

        //Patient fetch Doctors
        if (user.getAuthorities().contains(new SimpleGrantedAuthority(Roles.PATIENT.toString()))) {
            if (role == Roles.DOCTOR) {
                List<Long> ids = authService.getUsersByRoleAndStatus(role, true);
                resultPage = userProfileRepository.findByIdInAnd(ids, q, p);

                List<ExtendedProfileResponse> res = resultPage.getContent().stream()
                        .map(e -> ExtendedProfileResponse
                                .create((UserProfileEntity) e[0], (String) e[1], scheduleService.getScheduleByDoctorId(((UserProfileEntity) e[0]).getId()))).toList();
                return ResponseEntity.ok(new PageImpl<>(res, p, resultPage.getTotalElements()));

            } else {
                throw new RuntimeException("Access denied");
            }
        }
        //Doctor fetch Patients
        if (user.getAuthorities().contains(new SimpleGrantedAuthority(Roles.DOCTOR.toString()))) {
            if (role == Roles.PATIENT) {
                List<Long> ids = authService.getUsersByRoleAndStatus(role, true);
                resultPage = userProfileRepository.findByIdInAnd(ids, q, p);

                List<ExtendedProfileResponse> res = resultPage.getContent().stream()
                        .map(e -> ExtendedProfileResponse
                                .create((UserProfileEntity) e[0], (String) e[1], null)).toList();
                return ResponseEntity.ok(new PageImpl<>(res, p, resultPage.getTotalElements()));

            } else {
                throw new RuntimeException("Access denied");
            }
        }

        if (role != null) {
            List<Long> ids = authService.getUsersByRoleAndStatus(role, null);
            ids.remove(user.getId());
            resultPage = userProfileRepository.findByIdInAnd(ids, q, p);
        } else {
            if (!user.getAuthorities().contains(new SimpleGrantedAuthority(Roles.ADMIN.toString()))) {
                throw new RuntimeException("Access denied");
            }
            resultPage = userProfileRepository.findByQuery(q, p);
        }

        List<UserProfileResponse> res = resultPage.getContent().stream()
                .map(e -> UserProfileResponse.create((UserProfileEntity) e[0], (String) e[1])).toList();

        return ResponseEntity.ok(new PageImpl<>(res, p, resultPage.getTotalElements()));

    }

    ResponseEntity<?> getUserById(Long id) {
        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Roles role = authService.getRoleById(id);
        String email = null;
        if (!user.getId().equals(id)) {
            if (user.getAuthorities().contains(new SimpleGrantedAuthority(Roles.PATIENT.toString()))) {
                if (role != Roles.DOCTOR) {
                    throw new RuntimeException("Access denied");
                }
            }
            if (user.getAuthorities().contains(new SimpleGrantedAuthority(Roles.DOCTOR.toString()))) {
                if (role != Roles.PATIENT) {
                    throw new RuntimeException("Access denied");
                }
            }
        }
        UserProfileEntity finded = userProfileRepository.findById(id).orElseThrow();
        if (user.getAuthorities().contains(new SimpleGrantedAuthority(Roles.DOCTOR.toString())) && role == Roles.PATIENT) {
            email = authService.getEmailById(id).getBody();
        }
        if (user.getId().equals(finded.getId())) {
            email = authService.getEmailById(id).getBody();
        }
        ExtendedProfileResponse res = ExtendedProfileResponse.create(finded, email, scheduleService.getScheduleByDoctorId(finded.getId()));
        return ResponseEntity.ok(res);
    }

    @EventListener
    ResponseEntity<?> createUser(UserRegisterEvent r) {
        return ResponseEntity.ok(userProfileRepository.save(UserProfileEntity.create(r)));
    }

    @PreAuthorize("hasAuthority('ADMIN') or @decider.tokenIdEqualsIdFromRequest(#r.id)")
    ResponseEntity<?> updateUser(UserProfileUpdateRequest r) {


        UserProfileEntity user = userProfileRepository.findById(r.getId()).orElseThrow(() -> new RuntimeException("User Not Found"));
        modelMapper.map(r, user);
        UserProfileEntity newUser = userProfileRepository.save(user);
        if (r.getEmail() != null) {
            authService.update(UpdateRequest.create(r.getId(), r.getEmail()));
        }
        authService.activateUser(r.getId());
        ExtendedProfileResponse res = ExtendedProfileResponse.create(newUser, r.getEmail(), scheduleService.getScheduleByDoctorId(newUser.getId()));
        return ResponseEntity.ok(res);
    }

    @PreAuthorize("hasAuthority('ADMIN') or @decider.tokenIdEqualsIdFromRequest(#id)")
    ResponseEntity<?> updatePhoto(MultipartFile photo, Long id) {

        String filename = photo.getOriginalFilename();
        String extention = "";
        extention = filename.substring(filename.lastIndexOf("."));

        String uploadFilename = id + extention;

        Path filePath;
        try {

            Path uploadPath = Paths.get(UPLOAD_DIRECTORY.concat("/avatars"));
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            filePath = uploadPath.resolve(uploadFilename);
            photo.transferTo(filePath.toFile());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        UserProfileEntity user = userProfileRepository.findById(id).orElseThrow(() -> new RuntimeException("User Not Found"));
        user.setAvatar(filePath.toString());
        UserProfileEntity newUser = userProfileRepository.save(user);
        ExtendedProfileResponse res = ExtendedProfileResponse.create(newUser, authService.getEmailById(id).getBody(), scheduleService.getScheduleByDoctorId(newUser.getId()));
        return ResponseEntity.ok(res);
    }

    @PreAuthorize("hasAuthority('ADMIN') or @decider.tokenIdEqualsIdFromRequest(#id)")
    ResponseEntity<?> deletePhoto(Long id) {
        UserProfileEntity user = userProfileRepository.findById(id).orElseThrow();
        File photo = new File(user.getAvatar());
        if (photo.delete()) {
            user.setAvatar(null);
            userProfileRepository.save(user);
        }
        return ResponseEntity.ok().build();
    }

    @EventListener
    ResponseEntity<?> deleteUser(UserDeleteEvent e) {
        deletePhoto(e.getId());
        userProfileRepository.deleteById(e.getId());
        return ResponseEntity.ok("User Deleted");
    }

    public UserProfileEntity getUserProfile(Long id) {
        return userProfileRepository.findById(id).orElseThrow();
    }
}
