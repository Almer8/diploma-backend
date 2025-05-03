package org.example.diplomabackend.auth.entities;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateRequest {

    private long id;
    private String email;
    private String password;
    private String newPassword;
    private boolean isUserSetup;
    private String role;

    public static UpdateRequest create(Long id, String email){
        return UpdateRequest
                .builder()
                .id(id)
                .email(email)
                .build();
    }

}
