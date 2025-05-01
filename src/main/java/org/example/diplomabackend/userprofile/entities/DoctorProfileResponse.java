package org.example.diplomabackend.userprofile.entities;

import lombok.Builder;
import lombok.Data;
import org.example.diplomabackend.schedule.entities.ScheduleEntity;

import java.sql.Date;

@Data
@Builder
public class DoctorProfileResponse {
    private Long id;
    private String email;
    private String name;
    private String patronymic;
    private String surname;
    private Boolean sex;
    private Date birthday;
    private String avatar;
    private ScheduleEntity schedule;

    public static DoctorProfileResponse create(UserProfileEntity e, String email, ScheduleEntity schedule) {
        return DoctorProfileResponse.builder()
                .id(e.getId())
                .email(email)
                .name(e.getName())
                .patronymic(e.getPatronymic())
                .surname(e.getSurname())
                .sex(e.getSex())
                .birthday(e.getBirthday())
                .avatar(e.getAvatar())
                .schedule(schedule)
                .build();
    }
}
