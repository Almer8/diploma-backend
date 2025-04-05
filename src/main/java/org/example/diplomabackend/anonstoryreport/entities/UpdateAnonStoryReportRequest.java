package org.example.diplomabackend.anonstoryreport.entities;

import lombok.Data;

@Data
public class UpdateAnonStoryReportRequest {
    private Long id;
    private Long story_id;
    private Long submitter_id;
    private Long admin_id;
    private String topic;
    private String content;
    private String report_feedback;
    private AnonStoryReportStatus status;
}
