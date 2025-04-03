package org.example.diplomabackend.auth.entities;

import jakarta.persistence.*;
import lombok.*;
import org.example.diplomabackend.auth.utils.Roles;

@Entity
@Table(name="users")
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
