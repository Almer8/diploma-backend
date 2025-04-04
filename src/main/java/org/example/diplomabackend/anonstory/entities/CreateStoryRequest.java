package org.example.diplomabackend.anonstory.entities;

import lombok.Data;

import java.util.List;

@Data
public class CreateStoryRequest {
    private String title;
    private String displayed_name;
    private List<Long> tags;
    private String content;
}
