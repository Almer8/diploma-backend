package org.example.diplomabackend.auth;

import org.example.diplomabackend.auth.entities.AuthEntity;
import org.example.diplomabackend.utils.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthRepository extends JpaRepository<AuthEntity, Long> {
    Boolean existsByEmail(String email);
    Optional<AuthEntity> findByEmail(String email);
    List<AuthEntity> findAllByRole(Roles role);
}
