package org.example.diplomabackend.ticket.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Entity(name = "tickets")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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
    private TicketStatus status;

    public static TicketEntity create(CreateTicketRequest r, Long submitter_id){
        return TicketEntity.builder()
                .submitter_id(submitter_id)
                .topic(r.getTopic())
                .content(r.getContent())
                .submit_date(new Date(System.currentTimeMillis()))
                .status(TicketStatus.CREATED)
                .build();
    }
}
