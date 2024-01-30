package tech.ayot.ticket.backend.service.ticket;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
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
import tech.ayot.ticket.backend.model.enumuration.TicketType;
import tech.ayot.ticket.backend.model.product.Product;
import tech.ayot.ticket.backend.model.ticket.Message;
import tech.ayot.ticket.backend.model.ticket.Ticket;
import tech.ayot.ticket.backend.model.user.User;
import tech.ayot.ticket.backend.repository.product.ProductRepository;
import tech.ayot.ticket.backend.repository.ticket.MessageRepository;
import tech.ayot.ticket.backend.repository.ticket.TicketRepository;
import tech.ayot.ticket.backend.service.auth.AuthenticationService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
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
        @RequestParam @NotNull Integer page,
        @RequestParam(required = false) Integer pageSize,
        @RequestParam(required = false) String order,
        @RequestParam(required = false) Sort.Direction direction,
        @RequestParam(required = false) TicketType type,
        @RequestParam(required = false) String productName,
        @RequestParam(required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date createdAfter,
        @RequestParam(required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date createdBefore,
        @RequestParam(required = false) TicketStatus status
    ) {
        User user = authenticationService.getCurrentUser();
        ListTicketRequest request = new ListTicketRequest(
            page,
            pageSize,
            order,
            direction,
            type,
            productName,
            createdAfter,
            createdBefore,
            status
        );
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
        @RequestParam @NotNull Integer page,
        @RequestParam(required = false) Integer pageSize,
        @RequestParam(required = false) String order,
        @RequestParam(required = false) Sort.Direction direction,
        @RequestParam(required = false) TicketType type,
        @RequestParam(required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date createdAfter,
        @RequestParam(required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date createdBefore,
        @RequestParam(required = false) TicketStatus status
    ) {
        Product product = productRepository.findProductById(productId);
        if (product == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found!");
        }

        ListTicketRequest request = new ListTicketRequest(
            page,
            pageSize,
            order,
            direction,
            type,
            null,
            createdAfter,
            createdBefore,
            status
        );
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
        Date createdAfter = request.createdAfter() == null ? Date.from(Instant.EPOCH) : request.createdAfter();
        Date createdBefore = request.createdBefore() == null ? Date.from(Instant.now()) : request.createdBefore();
        Page<Ticket> tickets = ticketRepository.listAllByUser(
            userId,
            request.type(),
            productName,
            createdAfter,
            createdBefore,
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
