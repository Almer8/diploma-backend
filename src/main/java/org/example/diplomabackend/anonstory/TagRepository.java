package org.example.diplomabackend.anonstory;

import org.example.diplomabackend.anonstory.entities.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<TagEntity, Long> {

}
