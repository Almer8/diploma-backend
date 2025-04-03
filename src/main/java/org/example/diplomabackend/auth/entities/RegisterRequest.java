package org.example.diplomabackend.auth.entities;

import lombok.Data;

@Data
public class RegisterRequest {

    private String email;
    private String password;
    private String confirmPassword;
    private String role;

}
