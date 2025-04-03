package org.example.diplomabackend.auth.config;

import org.example.diplomabackend.auth.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class Decider {

    public static boolean tokenIdEqualsIdFromRequest(Long id){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

        return userDetails.getId().equals(id);
    }
}
