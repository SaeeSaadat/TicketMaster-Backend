package tech.ayot.ticket.backend.service.ticket;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tech.ayot.ticket.backend.annotation.CheckRole;
import tech.ayot.ticket.backend.dto.ticket.MessageDto;
import tech.ayot.ticket.backend.dto.ticket.TicketDto;
import tech.ayot.ticket.backend.dto.ticket.request.CreateTicketRequest;
import tech.ayot.ticket.backend.dto.ticket.request.ListTicketRequest;
import tech.ayot.ticket.backend.dto.ticket.request.UpdateTicketRequest;
import tech.ayot.ticket.backend.dto.ticket.response.ListTicketResponse;
import tech.ayot.ticket.backend.dto.ticket.response.ListUserTicketsProductsResponse;
import tech.ayot.ticket.backend.dto.ticket.response.ViewTicketResponse;
import tech.ayot.ticket.backend.model.enumuration.Role;
import tech.ayot.ticket.backend.model.enumuration.TicketStatus;
import tech.ayot.ticket.backend.model.product.Product;
import tech.ayot.ticket.backend.model.ticket.Message;
import tech.ayot.ticket.backend.model.ticket.Ticket;
import tech.ayot.ticket.backend.model.user.User;
import tech.ayot.ticket.backend.repository.product.ProductRepository;
import tech.ayot.ticket.backend.repository.ticket.MessageRepository;
import tech.ayot.ticket.backend.repository.ticket.TicketRepository;
import tech.ayot.ticket.backend.service.auth.AuthenticationService;

import java.util.ArrayList;
import java.util.List;

import static tech.ayot.ticket.backend.configuration.WebMvcConfiguration.PRODUCT_ID_PATH_VARIABLE_NAME;

@RestController
@RequestMapping("/api")
public class TicketService {

    private final AuthenticationService authenticationService;
    private final ProductRepository productRepository;
    private final TicketRepository ticketRepository;
    private final MessageRepository messageRepository;

    public TicketService(
        AuthenticationService authenticationService,
        ProductRepository productRepository,
        TicketRepository ticketRepository,
        MessageRepository messageRepository
    ) {
        this.authenticationService = authenticationService;
        this.productRepository = productRepository;
        this.ticketRepository = ticketRepository;
        this.messageRepository = messageRepository;
    }


    @Transactional
    @PostMapping(
        value = {"/product/{" + PRODUCT_ID_PATH_VARIABLE_NAME + "}/ticket"},
        consumes = {MediaType.APPLICATION_JSON_VALUE},
        produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<Long> create(
        @PathVariable Long productId,
        @Valid @RequestBody CreateTicketRequest request
    ) {
        Product product = productRepository.findProductById(productId);
        if (product == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found!");
        }

        Ticket ticket = new Ticket();
        ticket.setType(request.type());
        ticket.setProduct(product);
        ticket.setTitle(request.title());
        ticket.setDescription(request.description());
        ticket.setDeadline(request.deadline());
        ticket.setStatus(TicketStatus.OPEN);
        ticketRepository.save(ticket);

        return new ResponseEntity<>(ticket.getId(), HttpStatus.OK);
    }

    @GetMapping(
        value = {"/product/{" + PRODUCT_ID_PATH_VARIABLE_NAME + "}/ticket/{id}"},
        produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<ViewTicketResponse> view(
        @PathVariable Long productId,
        @PathVariable Long id
    ) {
        if (!productRepository.existsById(productId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found!");
        }

        Ticket ticket = ticketRepository.findTicketById(id);
        if (ticket == null || !ticket.getProduct().getId().equals(productId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found!");
        }

        List<Message> messages = messageRepository.findByTicketId(ticket.getId());

        ViewTicketResponse response = new ViewTicketResponse(
            ticket.getId(),
            ticket.getCreationDate(),
            ticket.getType(),
            ticket.getTitle(),
            ticket.getDescription(),
            ticket.getDeadline(),
            ticket.getStatus(),
            messages.stream().map(message -> new MessageDto(
                message.getId(),
                message.getCreatedBy().getId(),
                message.getCreatedBy().getUsername(),
                message.getContent(),
                message.getCreationDate()
            )).toList()
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(
        value = {"/ticket"},
        produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<ListTicketResponse> list(
        @Valid @RequestBody ListTicketRequest request
    ) {
        User user = authenticationService.getCurrentUser();
        ListTicketResponse response = listTickets(request, request.productName(), user.getId());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @CheckRole(role = Role.ADMIN)
    @GetMapping(
        value = {"/product/{" + PRODUCT_ID_PATH_VARIABLE_NAME + "}/ticket"},
        produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<ListTicketResponse> listAdmin(
        @PathVariable Long productId,
        @Valid @RequestBody ListTicketRequest request
    ) {
        Product product = productRepository.findProductById(productId);
        if (product == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found!");
        }

        ListTicketResponse response = listTickets(request, product.getName(), null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @CheckRole(role = Role.ADMIN)
    @PutMapping(
        value = {"/product/{" + PRODUCT_ID_PATH_VARIABLE_NAME + "}/ticket/{id}"},
        consumes = {MediaType.APPLICATION_JSON_VALUE},
        produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<Void> update(
        @PathVariable Long productId,
        @PathVariable Long id,
        @Valid @RequestBody UpdateTicketRequest request
    ) {
        Product product = productRepository.findProductById(productId);
        if (product == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found!");
        }

        Ticket ticket = ticketRepository.findTicketById(id);
        if (ticket == null || !ticket.getProduct().getId().equals(productId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found!");
        }

        if (ticket.getStatus() == TicketStatus.CLOSED) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Ticket is closed!");
        }

        ticket.setStatus(request.status());
        ticketRepository.save(ticket);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(
        value = {"tickets/products"},
        produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<ListUserTicketsProductsResponse> listUserTicketsProducts() {
        User user = authenticationService.getCurrentUser();

        List<Ticket> tickets = ticketRepository.findTicketsByCreatedBy(user);
        List<String> productNames = new ArrayList<>();
        for (Ticket ticket: tickets) {
            productNames.add(ticket.getTitle());
        }

        ListUserTicketsProductsResponse response = new ListUserTicketsProductsResponse(productNames);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    private ListTicketResponse listTickets(
        ListTicketRequest request,
        String productName,
        Long userId
    ) {
        Pageable pageRequest;
        if (request.order() == null) {
            pageRequest = PageRequest.of(
                request.page(),
                request.pageSize() == null ? 10 : request.pageSize()
            );
        } else {
            pageRequest = PageRequest.of(
                request.page(),
                request.pageSize() == null ? 10 : request.pageSize(),
                Sort.by(request.direction() == null ? Sort.Direction.ASC : request.direction(), request.order())
            );
        }
        Page<Ticket> tickets = ticketRepository.listAllByUser(
            userId,
            request.type(),
            productName,
            request.createdAfter(),
            request.createdBefore(),
            request.status(),
            pageRequest
        );

        return new ListTicketResponse(
            tickets.getTotalPages(),
            tickets.getNumber(),
            tickets.getNumberOfElements(),
            tickets.getContent().stream().map(ticket -> new TicketDto(
                ticket.getId(),
                ticket.getCreationDate(),
                ticket.getType(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getDeadline(),
                ticket.getStatus(),
                ticket.getProduct().getId(),
                ticket.getProduct().getName()
            )).toList()
        );
    }
}
