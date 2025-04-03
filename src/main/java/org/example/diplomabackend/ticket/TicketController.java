package org.example.diplomabackend.ticket;

import lombok.RequiredArgsConstructor;
import org.example.diplomabackend.ticket.entities.CreateTicketRequest;
import org.example.diplomabackend.ticket.entities.UpdateTicketRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RequiredArgsConstructor
@RestController
@RequestMapping("/ticket")
public class TicketController {
    public final TicketService ticketService;

    @GetMapping
    public ResponseEntity<?> getTickets(
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size,
            @RequestParam("sortBy") String sortBy,
            @RequestParam("sortDirection") String sortDirection) {
        return ResponseEntity.ok(ticketService.getTickets(page, size, sortBy, sortDirection));

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTicket(@PathVariable("id") Long id) {
        return ResponseEntity.ok(ticketService.getTicket(id));

    }

    @PostMapping
    public ResponseEntity<?> createTicket(@RequestBody CreateTicketRequest r) {
        return ResponseEntity.ok(ticketService.createTicket(r));

    }
    @PutMapping
    public ResponseEntity<?> updateTicket(@RequestBody UpdateTicketRequest r) {
        return ResponseEntity.ok(ticketService.updateTicket(r));

    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTicket(@PathVariable("id") Long id) {
        return ResponseEntity.ok(ticketService.deleteTicket(id));

    }

}
