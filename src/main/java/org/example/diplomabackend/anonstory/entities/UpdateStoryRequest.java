package org.example.diplomabackend.anonstory.entities;

import lombok.Data;

import java.util.List;

@Data
public class UpdateStoryRequest {
    private Long id;
    private String title;
    private String displayed_name;
    private List<Long> tags;
    private String content;
}
