package org.example.diplomabackend.anonstoryreport.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Entity(name = "anonstories_reports")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnonStoryReportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false,name = "story_id")
    private Long storyId;
    @Column(nullable = false)
    private Long submitter_id;
    private Long admin_id;
    @Column(nullable = false)
    private String topic;
    @Column(nullable = false)
    private String content;
    @Column(nullable = false)
    private Date submit_date;
    private String report_feedback;
    @Column(nullable = false)
    private AnonStoryReportStatus status;

    public static AnonStoryReportEntity create(CreateAnonStoryReportRequest r, Long submitter_id){
        return AnonStoryReportEntity.builder()
                .storyId(r.getStory_id())
                .submitter_id(submitter_id)
                .topic(r.getTopic())
                .content(r.getContent())
                .submit_date(new Date(System.currentTimeMillis()))
                .status(AnonStoryReportStatus.CREATED)
                .build();
    }
}
