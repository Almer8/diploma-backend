package org.example.diplomabackend.auth.entities;

import lombok.Data;

@Data
public class UpdateRequest {

    private long id;
    private String email;
    private String password;
    private String newPassword;
    private boolean isUserSetup;
    private String role;

}
