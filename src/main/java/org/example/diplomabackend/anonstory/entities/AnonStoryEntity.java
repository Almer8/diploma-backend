package org.example.diplomabackend.anonstory.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "anon_stories")
public class AnonStoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(nullable = false)
    Long author_id;
    @Column(nullable = false)
    String displayed_name;
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "entity_tag",
            joinColumns = @JoinColumn(name = "anon_story_entity_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    List<TagEntity> tags;

    public static AnonStoryEntity create(CreateStoryRequest r, Long author_id, List<TagEntity> tags) {
        return AnonStoryEntity.builder()
                .author_id(author_id)
                .displayed_name(r.getDisplayed_name())
                .tags(tags)
                .build();
    }
}
