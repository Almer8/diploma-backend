package org.example.diplomabackend.auth.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.diplomabackend.utils.Roles;

@Entity(name = "users")
@Table
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;
    @Column(unique=true,nullable=false)
    String email;
    @Column(nullable=false)
    String password;
    @Column(nullable=false)
    Boolean isUserSetup;
    @Column(nullable=false)
    Roles role;

}
