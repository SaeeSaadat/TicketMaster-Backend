package tech.ayot.ticket.backend.service.ticket;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tech.ayot.ticket.backend.dto.ticket.request.CreateMessageRequest;
import tech.ayot.ticket.backend.model.enumuration.TicketStatus;
import tech.ayot.ticket.backend.model.ticket.Message;
import tech.ayot.ticket.backend.model.ticket.Ticket;
import tech.ayot.ticket.backend.repository.ticket.MessageRepository;
import tech.ayot.ticket.backend.repository.ticket.TicketRepository;
import tech.ayot.ticket.backend.service.auth.AuthenticationService;

@RestController
@RequestMapping("/api/message")
public class MessageService {

    private final AuthenticationService authenticationService;
    private final MessageRepository messageRepository;
    private final TicketRepository ticketRepository;

    public MessageService(
        AuthenticationService authenticationService,
        MessageRepository messageRepository,
        TicketRepository ticketRepository
    ) {
        this.authenticationService = authenticationService;
        this.messageRepository = messageRepository;
        this.ticketRepository = ticketRepository;
    }

    @Transactional
    @PostMapping(
        value = {"/{ticketId}"},
        consumes = {MediaType.APPLICATION_JSON_VALUE},
        produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> create(
        @PathVariable Long ticketId,
        @Valid @RequestBody CreateMessageRequest createMessageRequest
        ) {
        Ticket ticket = ticketRepository.findTicketById(ticketId);
        if (ticket == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found!");
        }

        if (ticket.getStatus() == TicketStatus.CLOSED) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Ticket is closed!");
        }

        Message message = new Message();
        message.setContent(createMessageRequest.content());
        message.setTicket(ticket);

        messageRepository.save(message);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
