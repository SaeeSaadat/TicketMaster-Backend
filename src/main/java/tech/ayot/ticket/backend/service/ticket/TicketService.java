package tech.ayot.ticket.backend.service.ticket;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.ayot.ticket.backend.repository.user.UserRepository;
import tech.ayot.ticket.backend.service.auth.AuthenticationService;

@RestController
@RequestMapping("/api/ticket")
public class TicketService {
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;

    public TicketService(AuthenticationService authenticationService, UserRepository userRepository) {
        this.authenticationService = authenticationService;
        this.userRepository = userRepository;
    }

    @Transactional
    @PostMapping(value = {""}, consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<?> create() {

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
