package ru.dz.aviation.web;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.dz.aviation.domain.SeatClass;
import ru.dz.aviation.domain.Ticket;
import ru.dz.aviation.dto.BookTicketRequest;
import ru.dz.aviation.service.TicketService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public ResponseEntity<Ticket> book(@Valid @RequestBody BookTicketRequest request) {
        Ticket ticket = ticketService.book(request);
        return ResponseEntity.created(URI.create("/api/tickets/" + ticket.getId())).body(ticket);
    }

    @GetMapping
    public List<Ticket> byClass(@RequestParam SeatClass seatClass) {
        return ticketService.findBySeatClass(seatClass);
    }
}
