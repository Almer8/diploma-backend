package org.example.diplomabackend.userprofile;

import lombok.RequiredArgsConstructor;
import org.example.diplomabackend.auth.entities.AuthService;
import org.example.diplomabackend.auth.security.CustomUserDetails;
import org.example.diplomabackend.userprofile.entities.UserProfileCreateRequest;
import org.example.diplomabackend.userprofile.entities.UserProfileEntity;
import org.example.diplomabackend.userprofile.entities.UserProfileResponse;
import org.example.diplomabackend.userprofile.entities.UserProfileUpdateRequest;
import org.example.diplomabackend.utils.Roles;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${filestorage.directory}")
    private String UPLOAD_DIRECTORY;

    ResponseEntity<?> getUsers(Roles role, String q, Integer page, Integer size, String sortBy, String sortDirection){

        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Sort sort;
        Sort.Direction direction = Sort.Direction.ASC;
        if (sortDirection.equalsIgnoreCase("desc")) {
            direction = Sort.Direction.DESC;
        }

        sort = Sort.by(direction,sortBy);
        PageRequest p = PageRequest.of(page, size, sort);
        Page<Object[]> resultPage;
        if(role != null){
            List<Long> ids = authService.getUsersByRole(role);
            ids.remove(user.getId());
            resultPage = userProfileRepository.findByIdInAnd(ids,q,p);
        }
        else{
            if(!user.getAuthorities().contains(new SimpleGrantedAuthority(Roles.ADMIN.toString()))){
                throw new RuntimeException("Access denied");
            }
            resultPage = userProfileRepository.findByQuery(q,p);
        }


        List<UserProfileResponse> res = resultPage.getContent().stream()
                .map(e -> UserProfileResponse.create((UserProfileEntity) e[0],(String) e[1])).toList();

        return ResponseEntity.ok(new PageImpl<>(res,p,resultPage.getTotalElements()));

    }

    @PreAuthorize("@decider.tokenIdEqualsIdFromRequest(#r.id)")
    ResponseEntity<?> createUser(UserProfileCreateRequest r){

        return ResponseEntity.ok(userProfileRepository.save(UserProfileEntity.create(r)));
    }

    @PreAuthorize("hasAuthority('ADMIN') or @decider.tokenIdEqualsIdFromRequest(#r.id)")
    ResponseEntity<?> updateUser(UserProfileUpdateRequest r, MultipartFile photo){

    Path filePath = null;
    if(photo != null && !photo.isEmpty()){
        String filename = photo.getOriginalFilename();
        String extention = "";
        if(filename != null && filename.contains(".")){
            extention = filename.substring(filename.lastIndexOf("."));
        }
        String uploadFilename = r.getId() + extention;

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
    }

    UserProfileEntity user = userProfileRepository.findById(r.getId()).orElseThrow(()->new RuntimeException("User Not Found"));
    modelMapper.map(r, user);
    if(filePath != null){
        user.setAvatar(filePath.toString());
    }
    return ResponseEntity.ok(userProfileRepository.save(user));
    }

    @PreAuthorize("hasAuthority('ADMIN') or @decider.tokenIdEqualsIdFromRequest(#id)")
    ResponseEntity<?> deletePhoto(Long id){
        UserProfileEntity user = userProfileRepository.findById(id).orElseThrow();
        File photo = new File(user.getAvatar());
            if(photo.delete()){
                user.setAvatar(null);
                userProfileRepository.save(user);
            }
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('ADMIN') or @decider.tokenIdEqualsIdFromRequest(#id)")
    ResponseEntity<?> deleteUser(Long id){
        deletePhoto(id);
        userProfileRepository.deleteById(id);
        return ResponseEntity.ok("User Deleted");
    }
}
