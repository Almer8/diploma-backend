package org.example.diplomabackend.auth;

import lombok.RequiredArgsConstructor;
import org.example.diplomabackend.auth.entities.AuthService;
import org.example.diplomabackend.auth.entities.LoginRequest;
import org.example.diplomabackend.auth.entities.RegisterRequest;
import org.example.diplomabackend.auth.entities.UpdateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    ResponseEntity<?> register(@RequestBody RegisterRequest r){
        return authService.register(r);
    }

    @PostMapping("/login")
    ResponseEntity<?> login(@RequestBody LoginRequest r){
        return authService.login(r);
    }
    @PutMapping("/update")
    ResponseEntity<?> update(@RequestBody UpdateRequest r){
        return authService.update(r);
    }
    @DeleteMapping("/delete/{id}")
    ResponseEntity<?> delete(@PathVariable Long id){
        return authService.delete(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserIsSetUp(@PathVariable Long id){
        return authService.getUserIsReady(id);
    }
}
