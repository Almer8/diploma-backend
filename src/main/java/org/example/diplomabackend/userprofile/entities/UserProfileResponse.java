package org.example.diplomabackend.userprofile.entities;

import lombok.Builder;
import lombok.Data;

import java.sql.Date;

@Data
@Builder
public class UserProfileResponse {
    private Long id;
    private String email;
    private String name;
    private String patronymic;
    private String surname;
    private Boolean sex;
    private Date birthday;
    private String avatar;

    public static UserProfileResponse create(UserProfileEntity e, String email) {
        return UserProfileResponse.builder()
                .id(e.getId())
                .email(email)
                .name(e.getName())
                .patronymic(e.getPatronymic())
                .surname(e.getSurname())
                .sex(e.getSex())
                .birthday(e.getBirthday())
                .avatar(e.getAvatar())
                .build();
    }
}
