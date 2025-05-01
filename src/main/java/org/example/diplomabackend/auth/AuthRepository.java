package org.example.diplomabackend.auth;

import org.example.diplomabackend.auth.entities.AuthEntity;
import org.example.diplomabackend.utils.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthRepository extends JpaRepository<AuthEntity, Long> {
    Boolean existsByEmail(String email);
    Optional<AuthEntity> findByEmail(String email);

    @Query("SELECT u from users u where u.role = :role and" +
            "(:isUserSetup IS NULL or u.isUserSetup = :isUserSetup)")
    List<AuthEntity> findAllByRoleAndAndIsUserSetup(@Param("role") Roles role, @Param("isUserSetup")Boolean isUserSetup);
}
