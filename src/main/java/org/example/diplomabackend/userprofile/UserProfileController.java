package org.example.diplomabackend.userprofile;

import lombok.RequiredArgsConstructor;
import org.example.diplomabackend.auth.entities.UserRegisterEvent;
import org.example.diplomabackend.userprofile.entities.UserProfileUpdateRequest;
import org.example.diplomabackend.utils.Roles;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserProfileController {

    private final UserProfileService userProfileService;


    @GetMapping
    public ResponseEntity<?> getUsers(
            @RequestParam(name = "role", required = false) Roles role,
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "page") Integer page,
            @RequestParam(name = "size") Integer size,
            @RequestParam(name = "sortBy") String sortBy,
            @RequestParam(name = "sortDirection") String sortDirection) {

        return userProfileService.getUsers(role, q, page, size, sortBy, sortDirection);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable("id") Long id) {
        return userProfileService.getUserById(id);
    }

    @PatchMapping(path = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateUser(
            @RequestPart(value = "r") UserProfileUpdateRequest r,
            @RequestPart(value = "photo",required = false) MultipartFile photo){
        return userProfileService.updateUser(r, photo);
    }
    @DeleteMapping("/delete/photo/{id}")
    public ResponseEntity<?> deletePhoto(@PathVariable Long id){
        return userProfileService.deletePhoto(id);
    }


}
