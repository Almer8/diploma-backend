package org.example.diplomabackend.auth.security;


import lombok.RequiredArgsConstructor;
import org.example.diplomabackend.auth.AuthRepository;
import org.example.diplomabackend.auth.entities.AuthEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final AuthRepository repository;
    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AuthEntity user = repository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(username));
        return new CustomUserDetails(user);
    }
}
