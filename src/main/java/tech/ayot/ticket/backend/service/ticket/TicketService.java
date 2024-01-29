package tech.ayot.ticket.backend.service.ticket;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tech.ayot.ticket.backend.dto.ticket.request.CreateTicketRequest;
import tech.ayot.ticket.backend.model.product.Product;
import tech.ayot.ticket.backend.model.ticket.BugTicket;
import tech.ayot.ticket.backend.model.ticket.QuestionTicket;
import tech.ayot.ticket.backend.model.ticket.SuggestionTicket;
import tech.ayot.ticket.backend.model.ticket.Ticket;
import tech.ayot.ticket.backend.model.user.User;
import tech.ayot.ticket.backend.repository.product.ProductRepository;
import tech.ayot.ticket.backend.repository.ticket.TicketRepository;
import tech.ayot.ticket.backend.repository.user.UserRepository;
import tech.ayot.ticket.backend.service.auth.AuthenticationService;

@RestController
@RequestMapping("/api/ticket")
public class TicketService {
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final TicketRepository ticketRepository;

    public TicketService(AuthenticationService authenticationService, UserRepository userRepository,
                         ProductRepository productRepository, TicketRepository ticketRepository) {
        this.authenticationService = authenticationService;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.ticketRepository = ticketRepository;
    }

    @Transactional
    @PostMapping(value = {""}, consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<Long> create(@RequestBody CreateTicketRequest request) {
        if (!productRepository.existsProductByName(request.productName())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found!");
        }

        User user = authenticationService.getCurrentUser();
        Product product = productRepository.findProductByName(request.productName());
        long ticketId = switch (request.type()) {
            case "bug" -> this.createBugTicket(request, user, product);
            case "suggestion" -> this.createSuggestionTicket(request, user, product);
            case "question" -> this.createQuestionTicket(request, user, product);
            default -> 0;
        };

        return new ResponseEntity<>(ticketId, HttpStatus.OK);
    }

    @GetMapping

    private Long createBugTicket(CreateTicketRequest request, User user, Product product) {
        BugTicket ticket = new BugTicket();
        ticket.setTitle(request.title());
        ticket.setDescription(request.description());
        ticket.setUser(user);
        ticket.setDeadline(request.deadline());
        ticket.setProduct(product);
        ticketRepository.save(ticket);

        return ticket.getId();
    }

    private Long createQuestionTicket(CreateTicketRequest request, User user, Product product) {
        QuestionTicket ticket = new QuestionTicket();
        ticket.setTitle(request.title());
        ticket.setDescription(request.description());
        ticket.setUser(user);
        ticket.setDeadline(request.deadline());
        ticket.setProduct(product);
        ticketRepository.save(ticket);

        return ticket.getId();
    }

    private Long createSuggestionTicket(CreateTicketRequest request, User user, Product product) {
        SuggestionTicket ticket = new SuggestionTicket();
        ticket.setTitle(request.title());
        ticket.setDescription(request.description());
        ticket.setUser(user);
        ticket.setDeadline(request.deadline());
        ticket.setProduct(product);
        ticketRepository.save(ticket);

        return ticket.getId();
    }
}
