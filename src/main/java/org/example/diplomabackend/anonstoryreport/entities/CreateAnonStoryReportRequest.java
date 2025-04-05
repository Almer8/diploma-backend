package org.example.diplomabackend.anonstoryreport.entities;

import lombok.Data;

@Data
public class CreateAnonStoryReportRequest {

    private Long story_id;
    private String topic;
    private String content;
}
