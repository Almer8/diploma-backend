package org.example.diplomabackend.userprofile.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.diplomabackend.auth.entities.UserRegisterEvent;

import java.sql.Date;
@Data
@Entity(name = "user_profiles")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileEntity {

    @Id
    private Long id;
    private String name;
    private String patronymic;
    private String surname;
    private Boolean sex;
    private Date birthday;
    private String avatar;

    public static UserProfileEntity create(UserRegisterEvent r) {
        return UserProfileEntity
                .builder()
                .id(r.getId())
                .build();
    }
}
