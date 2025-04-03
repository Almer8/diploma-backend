package org.example.diplomabackend.ticket;

import lombok.RequiredArgsConstructor;
import org.example.diplomabackend.auth.security.CustomUserDetails;
import org.example.diplomabackend.ticket.entities.CreateTicketRequest;
import org.example.diplomabackend.ticket.entities.TicketEntity;
import org.example.diplomabackend.ticket.entities.TicketStatus;
import org.example.diplomabackend.ticket.entities.UpdateTicketRequest;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final ModelMapper modelMapper;

    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> getTickets(Integer page, Integer size, String sortBy, String sortDirection) {
        Sort sort = Sort.unsorted();

        if(sortBy != null && !sortBy.isEmpty()){
            Sort.Direction direction = Sort.Direction.ASC;
            if(sortDirection != null && sortDirection.equalsIgnoreCase("desc")) {
                direction = Sort.Direction.DESC;
            }
            sort = Sort.by(direction, sortBy);
        }
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Page<TicketEntity> tickets = ticketRepository.findAllByStatus(pageRequest, TicketStatus.CREATED);
        return ResponseEntity.ok(tickets);
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> getTicket(Long id) {
        return ResponseEntity.ok(ticketRepository.findById(id));
    }
    @PreAuthorize("hasAuthority('PATIENT') or hasAuthority('DOCTOR')")
    public ResponseEntity<?> createTicket(CreateTicketRequest r) {
        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return ResponseEntity.ok(ticketRepository.save(TicketEntity.create(r,user.getId())));

    }
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> updateTicket(UpdateTicketRequest r) {
        TicketEntity ticket = ticketRepository.findById(r.getId()).orElseThrow();
        modelMapper.map(r, ticket);

        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        ticket.setAdmin_id(user.getId());

        return ResponseEntity.ok(ticketRepository.save(ticket));
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> deleteTicket(Long id) {
        TicketEntity ticket = ticketRepository.findById(id).orElseThrow();
        ticketRepository.delete(ticket);
        return ResponseEntity.ok("Ticket deleted");
    }
}
