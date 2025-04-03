package org.example.diplomabackend.auth.entities;

import lombok.RequiredArgsConstructor;
import org.example.diplomabackend.auth.AuthRepository;
import org.example.diplomabackend.auth.security.CustomUserDetails;
import org.example.diplomabackend.auth.security.CustomUserDetailsService;
import org.example.diplomabackend.auth.security.JwtService;
import org.example.diplomabackend.auth.utils.Roles;
import org.example.diplomabackend.exception.UnauthorizedException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {


    private final AuthRepository authRepository;
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

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

    @PreAuthorize("hasAuthority('ADMIN') or @decider.tokenIdEqualsIdFromRequest(#r.id)")
    public ResponseEntity<?> delete(Long id) {
        authRepository.deleteById(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}
