package org.example.diplomabackend.ticket.entities;

import lombok.Data;

@Data
public class UpdateTicketRequest {
    private Long id;
    private Long submitter_id;
    private Long admin_id;
    private String topic;
    private String content;
    private String report_feedback;
    private TicketStatus status;
}
