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

    @PatchMapping(path = "/update")
    public ResponseEntity<?> updateUser(
            @RequestBody UserProfileUpdateRequest r){
        return userProfileService.updateUser(r);
    }
    @PatchMapping("/update/photo/{id}")
    public ResponseEntity<?> updateUserPhoto(
            @RequestPart(value = "photo") MultipartFile photo,
            @PathVariable Long id){
        return userProfileService.updatePhoto(photo,id);
    }
    @DeleteMapping("/delete/photo/{id}")
    public ResponseEntity<?> deletePhoto(@PathVariable Long id){
        return userProfileService.deletePhoto(id);
    }
}
