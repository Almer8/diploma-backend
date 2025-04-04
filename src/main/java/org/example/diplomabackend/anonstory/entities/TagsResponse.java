package org.example.diplomabackend.anonstory.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.util.ArrayList;

@Value
public class TagsResponse {

    @JsonProperty("_embedded")
    Embedded embedded;



    @Value
    private static class Embedded {
         ArrayList<TagResponse> tags;
    }
    public TagsResponse(ArrayList<TagResponse> tags) {
        this.embedded = new Embedded(tags);
    }

}
