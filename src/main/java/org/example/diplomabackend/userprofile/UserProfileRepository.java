package org.example.diplomabackend.userprofile;

import org.example.diplomabackend.userprofile.entities.UserProfileEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserProfileRepository extends JpaRepository<UserProfileEntity, Long> {


    @Query("SELECT up, ua.email from user_profiles up join users ua on up.id = ua.id WHERE " +
            "up.id IN :ids AND" +
            ":q IS NULL OR :q = ''" +
            "OR lower(ua.email) LIKE lower(concat('%',:q, '%'))" +
            "OR lower(up.name) LIKE lower(concat('%',:q, '%'))" +
            "OR lower(up.surname) LIKE lower(concat('%',:q, '%'))" +
            "OR lower(up.patronymic) LIKE lower(concat('%',:q, '%'))")
    Page<Object[]> findByIdInAnd(@Param("ids")List<Long> ids, @Param("q")String q, Pageable pageable);

    @Query("SELECT up, ua.email from user_profiles up join users ua on up.id = ua.id WHERE " +
            ":q IS NULL OR :q = ''" +
            "OR lower(ua.email) LIKE lower(concat('%',:q, '%'))" +
            "OR lower(up.name) LIKE lower(concat('%',:q, '%'))" +
            "OR lower(up.surname) LIKE lower(concat('%',:q, '%'))" +
            "OR lower(up.patronymic) LIKE lower(concat('%',:q, '%'))")
    Page<Object[]> findByQuery(@Param("q")String q, Pageable pageable);
}
