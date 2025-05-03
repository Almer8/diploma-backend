package org.example.diplomabackend.userprofile.entities;

import lombok.Data;

import java.sql.Date;

@Data
public class UserProfileUpdateRequest {
    private Long id;
    private String email;
    private String name;
    private String patronymic;
    private String surname;
    private Boolean sex;
    private Date birthday;

}
