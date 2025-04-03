package org.example.diplomabackend.ticket;

import org.example.diplomabackend.ticket.entities.TicketEntity;
import org.example.diplomabackend.ticket.entities.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<TicketEntity, Long> {
    Page<TicketEntity> findAllByStatus(PageRequest PageRequest, TicketStatus status);
}
