package org.example.diplomabackend.auth.entities;

import lombok.Data;

@Data
public class LoginRequest {

    private String email;
    private String password;

}
