package org.example.diplomabackend.ticket.entities;

import lombok.Data;

@Data
public class CreateTicketRequest {

    private String topic;
    private String content;
}
