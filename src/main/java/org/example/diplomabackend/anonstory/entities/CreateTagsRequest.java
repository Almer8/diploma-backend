package org.example.diplomabackend.anonstory.entities;

import lombok.Data;

import java.util.List;

@Data
public class CreateTagsRequest {

    private List<String> tags;
}
