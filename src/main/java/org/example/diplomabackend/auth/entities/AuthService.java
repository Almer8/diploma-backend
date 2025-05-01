package org.example.diplomabackend.auth.entities;

import lombok.RequiredArgsConstructor;
import org.example.diplomabackend.auth.AuthRepository;
import org.example.diplomabackend.auth.security.CustomUserDetails;
import org.example.diplomabackend.auth.security.CustomUserDetailsService;
import org.example.diplomabackend.auth.security.JwtService;
import org.example.diplomabackend.utils.Roles;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {


    private final AuthRepository authRepository;
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final ApplicationEventPublisher applicationEventPublisher;

    public ResponseEntity<?> register(RegisterRequest r) {
        if(authRepository.existsByEmail(r.getEmail())){
            throw new RuntimeException("Email already exists");
        }
        if(!r.getPassword().equals(r.getConfirmPassword())){
            throw new RuntimeException("Passwords do not match");
        }
        AuthEntity user = authRepository.save(
                AuthEntity.builder()
                        .email(r.getEmail())
                        .password(passwordEncoder.encode(r.getPassword()))
                        .isUserSetup(false)
                        .role(Roles.valueOf(r.getRole().toUpperCase()))
                        .build()

        );
        applicationEventPublisher.publishEvent(new UserRegisterEvent(this,user.getId()));

        if(r.getRole().equalsIgnoreCase(Roles.DOCTOR.toString())){
            applicationEventPublisher.publishEvent(new DoctorRegisterEvent(this,user.getId()));
        }

        String token = jwtService.generateToken(customUserDetailsService.loadUserByUsername(user.getEmail()));
        return ResponseEntity.ok(new JwtResponse(token));
    }

    public ResponseEntity<?> login(LoginRequest r) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        r.getEmail(),
                        r.getPassword())
        );

        final CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        final String token = jwtService.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(token));
    }

    @PreAuthorize("hasAuthority('ADMIN') or @decider.tokenIdEqualsIdFromRequest(#r.id)")
    public ResponseEntity<?> update(UpdateRequest r) {
        AuthEntity user = authRepository.findById(r.getId()).orElseThrow();
        user.setEmail(r.getEmail());

        if(r.getNewPassword() != null){
            user.setPassword(passwordEncoder.encode(r.getNewPassword()));
        }

        if(SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority(Roles.ADMIN.toString()))){
            user.setRole(Roles.valueOf(r.getRole().toUpperCase()));
        }
        user.setIsUserSetup(r.isUserSetup());
        authRepository.save(user);
        String newJwt = jwtService.generateToken(customUserDetailsService.loadUserByUsername(user.getEmail()));
        return ResponseEntity.ok(new JwtResponse(newJwt));
    }

    @PreAuthorize("hasAuthority('ADMIN') or @decider.tokenIdEqualsIdFromRequest(#id)")
    public ResponseEntity<?> delete(Long id) {
        applicationEventPublisher.publishEvent(new UserDeleteEvent(this,id));
        authRepository.deleteById(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    public ResponseEntity<String> getEmailById(Long id) {
        AuthEntity user = authRepository.findById(id).orElseThrow();
        return ResponseEntity.ok(user.getEmail());
    }

    public Roles getRoleById(Long id){
        return authRepository.findById(id).orElseThrow().getRole();
    }

    public List<Long> getUsersByRoleAndStatus(Roles role, Boolean status) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(userDetails.getAuthorities().contains(new SimpleGrantedAuthority(Roles.PATIENT.toString())) && role == Roles.DOCTOR ||
        userDetails.getAuthorities().contains(new SimpleGrantedAuthority(Roles.DOCTOR.toString())) && role == Roles.PATIENT ||
        userDetails.getAuthorities().contains(new SimpleGrantedAuthority(Roles.ADMIN.toString())) && role != Roles.ADMIN){
            return(authRepository.findAllByRoleAndAndIsUserSetup(role, status).stream().map(e ->e.getId())).toList();
        }
        throw new RuntimeException("Not authorized");

    }
    @PreAuthorize("@decider.tokenIdEqualsIdFromRequest(#id)")
    public ResponseEntity<?> getUserIsReady(Long id){
        AuthEntity user = authRepository.findById(id).orElseThrow();
        return ResponseEntity.ok(user.getIsUserSetup());
    }
    @PreAuthorize("@decider.tokenIdEqualsIdFromRequest(#id)")
    public ResponseEntity<?> activateUser(Long id){
        AuthEntity user = authRepository.findById(id).orElseThrow();
        user.setIsUserSetup(true);
        return ResponseEntity.ok(authRepository.save(user));
    }
}
