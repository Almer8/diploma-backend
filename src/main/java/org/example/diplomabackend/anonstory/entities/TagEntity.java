package org.example.diplomabackend.anonstory.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity(name = "tags")
@Data
public class TagEntity {

    public TagEntity(){}
    public TagEntity(String name){
        this.name = name;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(nullable = false, unique = true)
    String name;

    @ManyToMany(mappedBy = "tags")
    @JsonIgnore
    List<AnonStoryEntity> storyEntities;

    public static TagEntity create(String name){
        return new TagEntity(name);
    }
}
