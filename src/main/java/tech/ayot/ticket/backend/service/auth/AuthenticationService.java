package tech.ayot.ticket.backend.service.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.session.Session;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tech.ayot.ticket.backend.dto.auth.LoginRequest;
import tech.ayot.ticket.backend.dto.auth.LoginResponse;
import tech.ayot.ticket.backend.dto.auth.RegisterRequest;
import tech.ayot.ticket.backend.dto.auth.UserDto;
import tech.ayot.ticket.backend.model.user.User;
import tech.ayot.ticket.backend.repository.user.UserRepository;

import javax.validation.Valid;

/**
 * Authentication service
 */
@RestController
@RequestMapping("/api/auth")
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;

    private final SessionService<Session> sessionService;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(
        AuthenticationManager authenticationManager,
        SessionService<Session> sessionService,
        UserRepository userRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.sessionService = sessionService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    /**
     * Logs in the user.
     *
     * @param request      The HTTP request.
     * @param session      The HTTP session.
     * @param loginRequest The login request.
     * @return A login response object if the login is successful, or an error object if any error occurs during the login process.
     */
    @PostMapping(value = {"/login"},consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<LoginResponse> login(
        HttpServletRequest request,
        HttpSession session,
        @Valid @RequestBody LoginRequest loginRequest
    ) {
        session.invalidate();

        // Check if username and password exist
        if (loginRequest.getUsername() == null || loginRequest.getPassword() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }

        // Authenticate user
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            loginRequest.getUsername().toLowerCase(),
            loginRequest.getPassword()
        );
        Authentication authentication;
        try {
            // If user is already authenticated, use authToken,
            // otherwise, present the token to AuthenticationManager for re-authentication
            authentication = authToken.isAuthenticated() ? authToken : authenticationManager.authenticate(authToken);
        } catch (AccountExpiredException ignored) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account is expired");
        } catch (LockedException ignored) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account is locked");
        } catch (DisabledException ignored) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account is disabled");
        } catch (AuthenticationException ignored) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }

        // Get user details
        UserDto userDto = (UserDto) authentication.getPrincipal();

        // Create session
        sessionService.createSession(
            request,
            authentication,
            userDto.getUsername()
        );

        // Return login response
        LoginResponse loginResponse = new LoginResponse(
            userDto.getId(),
            userDto.getUsername()
        );
        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }

    /**
     * Logs out the user.
     *
     * @param request  The HTTP request.
     * @param response The HTTP response.
     * @return An empty OK if successful
     */
    @PostMapping(value = {"/logout"})
    public ResponseEntity<Void> logout(
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, response, null);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Registers the new user.
     *
     * @param request The register request
     * @return An empty OK if successful, CONFLICT if username exists
     */
    @PostMapping(value = {"/register"}, consumes = {"application/json"})
    public ResponseEntity<Void> register(
        @Valid @RequestBody RegisterRequest request
    ) {
        // Check if user with the username already exists.
        User user = userRepository.findUserByUsername(request.getUsername());
        if (user != null) {
            throw  new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }

        // Create user
        user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * @return Login response of current logged-in user
     */
    @GetMapping(value = {"/user"}, produces = {"application/json"})
    public ResponseEntity<LoginResponse> currentUser() {
        // Get current user DTO
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.getPrincipal() instanceof UserDto;
        UserDto userDto = isAuthenticated ? (UserDto) authentication.getPrincipal() : null;

        // Return login response with null values if user details is null
        if (userDto == null) {
            LoginResponse loginResponse = new LoginResponse(
                null,
                null
            );
            return new ResponseEntity<>(loginResponse, HttpStatus.OK);
        }

        // Update user's current session if user is updated
        User user = userRepository.findUserByUsername(userDto.getUsername());
        if (user.getLastModifiedDate() != null
            && (userDto.getModifiedDate() == null
            || userDto.getModifiedDate().compareTo(user.getLastModifiedDate()) != 0)) {
            sessionService.updateCurrentSession(new UserDto(user));
        }

        // Return login response with current user's id and username
        LoginResponse loginResponse = new LoginResponse(
            userDto.getId(),
            userDto.getUsername()
        );
        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }
}
