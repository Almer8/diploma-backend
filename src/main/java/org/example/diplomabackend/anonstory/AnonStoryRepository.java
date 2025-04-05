package org.example.diplomabackend.anonstory;

import org.example.diplomabackend.anonstory.entities.AnonStoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AnonStoryRepository extends JpaRepository<AnonStoryEntity,Long> {

    @Query("SELECT DISTINCT s FROM anon_stories s " +
            "LEFT JOIN s.tags t " +
            "WHERE (:q IS NULL OR :q = '' OR LOWER(s.displayed_name) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(t.name) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(s.content) LIKE LOWER(CONCAT('%', :q, '%')))")
    Page<AnonStoryEntity> findAllByQuery(@Param("q") String q, PageRequest p);
    boolean existsByTagsId(Long tagId);
}
